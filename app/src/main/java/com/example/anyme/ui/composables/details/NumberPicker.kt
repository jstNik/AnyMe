package com.example.anyme.ui.composables.details

import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.PointerInputChange
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign

@Composable
fun NumberPicker(
   initialValue: String,
   range: List<String>,
   textStyle: TextStyle,
   wrapUpChoices: Boolean = false,
   onItemSelection: (Int) -> Unit
) {

   if(wrapUpChoices && range.isEmpty()) error("If the range is empty the picker can't wrap up")

   val half = Int.MAX_VALUE.shr(1) / range.size * range.size

   // TODO Handle special cases like no numEps available and 0 eps watched
   val initialIndex = rememberSaveable {
      if(wrapUpChoices) {
         half + range.indexOf(initialValue)
      }
      else range.indexOf(initialValue)
   }
   val density = LocalDensity.current
   val focusManager = LocalFocusManager.current
   val keyboardController = LocalSoftwareKeyboardController.current
   val focusRequester = remember { FocusRequester() }

   val arrowSize =  24.dp
   var textHeight by remember { mutableStateOf(0.dp) }
   var boxWidth by remember { mutableStateOf(0.dp) }
   var alphaBasicFieldText by remember { mutableFloatStateOf(0F) }
   val arrowsPadding = 8.dp

   val coroutineScope = rememberCoroutineScope()
   val lazyListState = rememberLazyListState(initialIndex)
   val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
   val layoutInfo by remember {
      derivedStateOf { lazyListState.layoutInfo }
   }

   var selectedIndex by rememberSaveable { mutableIntStateOf(initialIndex) }
   var centralIndex by rememberSaveable { mutableIntStateOf(initialIndex) }
   val padding by remember {
      derivedStateOf {
         val viewPortSize = with(density) { layoutInfo.viewportSize.height.toDp() }
         val res = (viewPortSize - textHeight) / 2
         max(0.dp.value, res.value).dp
      }
   }

   val onVerticalDrag: (PointerInputChange, Float) -> Unit = { _, amount ->
      coroutineScope.launch {
         lazyListState.scrollBy(-amount)
      }
   }

   val onDragEnd: () -> Unit = {
      coroutineScope.launch {
         selectedIndex = centralIndex
         lazyListState.animateScrollToItem(centralIndex)
         onItemSelection(centralIndex)
      }
   }

   LaunchedEffect(layoutInfo.viewportSize) {
      lazyListState.scrollToItem(initialIndex)
   }

   LaunchedEffect(lazyListState) {
      snapshotFlow { lazyListState.firstVisibleItemScrollOffset }.collect {
         val visibleItems = layoutInfo.visibleItemsInfo
         if (visibleItems.isEmpty()) return@collect
         layoutInfo.visibleItemsInfo.minByOrNull {
            val offset = it.offset.toFloat()
            abs(offset + sign(offset) * it.size / 2F)
         }?.let { centerItem ->
            centralIndex = centerItem.index
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

      Box(
         contentAlignment = Alignment.Center,
         modifier = Modifier
            .height(arrowSize * 2 + arrowsPadding)
      ) {

         LazyColumn(
            state = lazyListState,
            flingBehavior = snapBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((-8).dp),
            userScrollEnabled = false,
            contentPadding = PaddingValues(vertical = padding),
            modifier = Modifier
               .height(arrowSize * 2 + arrowsPadding)
               .width(boxWidth)
               .align(Alignment.Center)
         ) {
            items(
               count = if (wrapUpChoices) Int.MAX_VALUE else range.size,
               key = { it },
            ) { idx ->

               val wrapIdx = idx.mod(range.size)

               val value = range[wrapIdx]

               val alphaToScale = layoutInfo.visibleItemsInfo.find {
                  it.index.mod(range.size) == wrapIdx
               }?.let { item ->
                  val distance = abs(item.offset.toFloat())
                  if(layoutInfo.viewportSize.height > 0) {
                     val fraction = (distance * 2F / layoutInfo.viewportSize.height)
                     val alphaFraction = (fraction * 2F).coerceIn(0F, 1F)
                     val scaleFraction = (fraction * 1.25F).coerceIn(0F, 1F)
                     lerp(1F, 0F, alphaFraction) to
                             lerp(1F, 0F, scaleFraction)
                  } else null
               } ?: (0F to 0F)

               Text(
                  text = value,
                  style = textStyle,
                  modifier = Modifier
                     .onGloballyPositioned {
                        if (selectedIndex.mod(range.size) == wrapIdx)
                           textHeight = with(density) { it.size.height.toDp() }
                     }
                     .alpha(
                        if (selectedIndex.mod(range.size) != wrapIdx || alphaBasicFieldText == 0F)
                           alphaToScale.first else 0F
                     )
                     .scale(alphaToScale.second)
               )
            }
         }

         var basicFieldTextValue by remember(selectedIndex) {
            mutableStateOf(
               range.getOrNull(
                  if (wrapUpChoices) selectedIndex.mod(range.size)
                  else selectedIndex
               ) ?: ""
            )
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
                  val valueIdx = range.indexOf(basicFieldTextValue)
                  var scrollJob: Job? = null
                  val newIdx = if (!wrapUpChoices && valueIdx in 0..<range.size) {
                     selectedIndex = valueIdx
                     valueIdx
                  } else if (wrapUpChoices) {
                     val offset = selectedIndex.mod(range.size)
                     selectedIndex = half + valueIdx
                     scrollJob = coroutineScope.launch {
                        lazyListState.scrollToItem(half + offset)
                     }
                     valueIdx
                  } else {
                     basicFieldTextValue = range[selectedIndex]
                     null
                  }

                  newIdx?.let {
                     coroutineScope.launch {
                        scrollJob?.join()
                        lazyListState.scrollToItem(selectedIndex)
                     }
                     onItemSelection(it)
                  }
                  focusManager.clearFocus()
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
                     onVerticalDrag = onVerticalDrag,
                     onDragEnd = onDragEnd
                  )
               }
         )

         Column {
            Icon(
               Icons.Outlined.KeyboardArrowUp,
               null,
               tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5F),
               modifier = Modifier
                  .size(arrowSize)
                  .clip(CircleShape)
                  .clickable {
                     var scrollJob: Job? = null
                     val newIndex =
                        if (selectedIndex in 1..<range.size) {
                           --selectedIndex
                        } else if((wrapUpChoices && selectedIndex in 1..Int.MAX_VALUE)) {
                           (--selectedIndex).mod(range.size)
                        } else if (wrapUpChoices) {
                           val offset = (selectedIndex - 1).mod(range.size)
                           selectedIndex = half + offset
                           scrollJob = coroutineScope.launch {
                              lazyListState.scrollToItem(selectedIndex + 1)
                           }
                           offset
                        } else null

                     newIndex?.let {
                        coroutineScope.launch {
                           scrollJob?.join()
                           lazyListState.animateScrollToItem(selectedIndex)
                        }
                        onItemSelection(it)
                     }
                  }
                  .pointerInput(Unit) {
                     detectVerticalDragGestures(
                        onVerticalDrag = onVerticalDrag,
                        onDragEnd = onDragEnd
                     )
                  }
            )

            Spacer(Modifier.height(arrowsPadding))

            Icon(
               Icons.Outlined.KeyboardArrowDown,
               null,
               tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5F),
               modifier = Modifier
                  .size(arrowSize)
                  .clip(CircleShape)
                  .clickable {
                     var scrollJob: Job? = null
                     val newIndex =
                        if (selectedIndex in 0..<(range.size - 1)) {
                           ++selectedIndex
                        } else if ((wrapUpChoices && selectedIndex in 0..<(Int.MAX_VALUE - 1))) {
                           (++selectedIndex).mod(range.size)
                        } else if (wrapUpChoices) {
                           val offset = (selectedIndex + 1).mod(range.size)
                           selectedIndex = half + offset
                           scrollJob = coroutineScope.launch {
                              lazyListState.scrollToItem(selectedIndex - 1)
                           }
                           offset
                        } else null

                     newIndex?.let {
                        coroutineScope.launch {
                           scrollJob?.join()
                           lazyListState.animateScrollToItem(selectedIndex)
                        }
                        onItemSelection(it)
                     }
                  }
                  .pointerInput(Unit) {
                     detectVerticalDragGestures(
                        onVerticalDrag = onVerticalDrag,
                        onDragEnd = onDragEnd
                     )
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
            "8",
            (0..26).toList().map{ "$it" },
            TitleStyle,
            wrapUpChoices = true
         ) {
//            Log.d("Number Picker", "Item selected of index $it")
         }
      }
   }
}
