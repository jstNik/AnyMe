package com.example.anyme.ui.composables.details

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.TitleStyle
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

@Composable
fun NumberPicker(
   initialValue: String,
   range: List<String>,
   textStyle: TextStyle,
   onItemSelection: (Int) -> Unit
) {

   // TODO Handle special cases like no numEps available and 0 eps watched
   val initialIndex = rememberSaveable { range.indexOf(initialValue) }
   val density = LocalDensity.current
   val focusManager = LocalFocusManager.current
   val keyboardController = LocalSoftwareKeyboardController.current
   val focusRequester = remember { FocusRequester() }

   var arrowUpHeight by remember { mutableStateOf(0.dp) }
   var upperTextHeight by remember{ mutableStateOf(0.dp) }
   var textHeight by remember { mutableStateOf(0.dp) }
   var lowerTextHeight by remember{ mutableStateOf(0.dp) }
   var boxWidth by remember { mutableStateOf(0.dp) }
   var arrowDownHeight by remember { mutableStateOf(0.dp) }
   var alphaBasicFieldText by remember { mutableFloatStateOf(0F) }

   val coroutineScope = rememberCoroutineScope()
   val lazyListState = rememberLazyListState(initialIndex)
   val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
   val layoutInfo by remember {
      derivedStateOf { lazyListState.layoutInfo }
   }

   var selectedIndex by rememberSaveable { mutableIntStateOf(initialIndex) }
   val padding by remember {
      derivedStateOf {
         val viewPortSize = with(density) { layoutInfo.viewportSize.height.toDp() }
         val res = (viewPortSize - textHeight) / 2
         max(0.dp.value, res.value).dp
      }
   }

   LaunchedEffect(layoutInfo.viewportSize) {
      lazyListState.scrollToItem(initialIndex)
   }

   LaunchedEffect(lazyListState) {
      snapshotFlow { lazyListState.firstVisibleItemIndex }.collect {
         val visibleItems = layoutInfo.visibleItemsInfo
         if (visibleItems.isEmpty()) return@collect
         layoutInfo.visibleItemsInfo.minByOrNull {
            val offset = it.offset.toFloat()
            abs(offset + sign(offset) * it.size / 2F)
         }?.let { centerItem ->
            selectedIndex = centerItem.index
         }
      }
   }

   if(boxWidth == 0.dp)
      Box(
         modifier = Modifier.alpha(0F)
      ){
         TitleSection(
            range.maxBy { it.length },
            modifier = Modifier.onGloballyPositioned{
               boxWidth = with(density) { it.size.width.toDp() }
            }
         )
      }

//   Row(
//      horizontalArrangement = Arrangement.Center,
//      verticalAlignment = Alignment.CenterVertically,
//      modifier = Modifier.clickable(
//         indication = null,
//         interactionSource = remember { MutableInteractionSource() }
//      ) {
//         focusManager.clearFocus()
//      }
//   ) {

      Box(
         contentAlignment = Alignment.Center
      ) {

         LazyColumn(
            state = lazyListState,
            flingBehavior = snapBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-10).dp),
            contentPadding = PaddingValues(
               top = padding,
               bottom = padding
            ),
            modifier = Modifier
               .padding(vertical = 8.dp)
               .align(Alignment.Center)
               .height(upperTextHeight + textHeight + lowerTextHeight)
         ) {
            items(
               count = range.size,
               key = { it },
            ) { idx ->

               val value = range[idx]

               val alphaToScale = layoutInfo.visibleItemsInfo.find { it.index == idx }?.let { item ->

                  val distance = abs(item.offset.toFloat())
                  val fraction = (distance * 2F / layoutInfo.viewportSize.height).coerceIn(0F, 1F)
                  lerp(1F, 0F, fraction * 2) to lerp(1F, 0F, fraction * 1.25F)
               } ?: (0F to 0F)

               Text(
                  text = value,
                  style = textStyle,
                  modifier = Modifier
                     .onGloballyPositioned {
                        if (selectedIndex == idx)
                           textHeight = with(density) { it.size.height.toDp() }
                        else if (selectedIndex - 1 == idx)
                           upperTextHeight = with(density) { it.size.height.toDp() }
                        else if (selectedIndex + 1 == idx)
                           lowerTextHeight = with(density) { it.size.height.toDp() }
                     }
                     .alpha(
                        if (selectedIndex != idx || alphaBasicFieldText == 0F)
                           alphaToScale.first else 0F
                     )
                     .scale(alphaToScale.second)
               )
            }
         }

         var basicFieldTextValue by remember(selectedIndex) {
            mutableStateOf(range.getOrNull(selectedIndex) ?: "")
         }

         BasicTextField(
            value = basicFieldTextValue,
            onValueChange = {
               basicFieldTextValue = it
            },
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(
               keyboardType = KeyboardType.Number,
               imeAction = ImeAction.Done,
               showKeyboardOnFocus = false
            ),
            keyboardActions = KeyboardActions(
               onDone = {
                  val newIdx = range.indexOf(basicFieldTextValue)
                  if (newIdx == -1)
                     basicFieldTextValue = range[selectedIndex]
                  else
                     coroutineScope.launch {
                        lazyListState.animateScrollToItem(newIdx)
                        onItemSelection(newIdx)
                     }
                  focusManager.clearFocus(true)
               }
            ),
            decorationBox = {
               it()
            },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
               .align(Alignment.Center)
               .focusRequester(focusRequester)
               .animateContentSize()
               .padding(horizontal = 2.dp)
               .height(textHeight)
               .width(boxWidth)
               .alpha(alphaBasicFieldText)
               .onFocusChanged {
                  alphaBasicFieldText = if (!it.isFocused) {
                     keyboardController?.hide()
                     0F
                  } else 1F
               }
               .pointerInput(Unit) {
                  detectVerticalDragGestures(
                     onVerticalDrag = { _, amount ->
                        coroutineScope.launch {
                           lazyListState.scrollBy(-amount)
                        }
                     },
                     onDragEnd = {
                        coroutineScope.launch {
                           lazyListState.scrollToItem(selectedIndex)
                           onItemSelection(selectedIndex)
                        }
                     }
                  )
               }
         )

         Column {
            Icon(
               Icons.Outlined.KeyboardArrowUp,
               null,
               tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5F),
               modifier = Modifier
                  .onGloballyPositioned {
                     arrowUpHeight = with(density) { it.size.height.toDp() }
                  }.clip(CircleShape)
                  .clickable {
                     coroutineScope.launch {
                        lazyListState.scrollToItem(selectedIndex - 1)
                        onItemSelection(selectedIndex - 1)
                     }
                  }
            )

            Spacer(
               Modifier
                  .padding(vertical = 0.dp)
                  .height(textHeight)
            )

            Icon(
               Icons.Outlined.KeyboardArrowDown,
               null,
               tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5F),
               modifier = Modifier
                  .onGloballyPositioned {
                     arrowDownHeight = with(density) { it.size.height.toDp() }
                  }.clip(CircleShape)
                  .clickable {
                     coroutineScope.launch {
                        lazyListState.scrollToItem(selectedIndex + 1)
                        onItemSelection(selectedIndex + 1)
                     }
                  }
            )

         }
      }
//   }

}

@Preview
@Composable
fun PreviewNumberPicker() {
   AnyMeTheme {

      Column(
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center,
         modifier = Modifier.fillMaxSize()
      ) {
         NumberPicker(
            "1147",
            (1..1147).toList().map{ "$it" },
            TitleStyle
         ) {
            Log.d("Number Picker", "Item selected of index $it")
         }
      }
   }
}
