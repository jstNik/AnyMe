package com.example.anyme.ui.composables.details

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
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
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.text.TextAutoSizeDefaults
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

interface WheelPickerBehavior{

   val size: Int
   fun getText(idx: Int): String?
   fun getIndexOf(string: String): Int
}

@Composable
fun WheelPicker(
   initialIndex: Int,
   behavior: WheelPickerBehavior,
   textStyle: TextStyle,
   textAutoSize: TextAutoSize? = null,
   wrapUpChoices: Boolean = false,
   enableTextFieldInput: Boolean = true,
   enableScrolling: Boolean = true,
   onItemSelection: (Int) -> Unit
) {

   if (wrapUpChoices && behavior.size == Int.MAX_VALUE) error("If the range is infinite the picker can't wrap up")

   val half = if(wrapUpChoices) Int.MAX_VALUE.shr(1) / behavior.size * behavior.size else 0

   // TODO Handle special cases like no numEps available and 0 eps watched
   val initialIdx = rememberSaveable(initialIndex) {
      if (wrapUpChoices) half + initialIndex
      else initialIndex
   }
   val density = LocalDensity.current
   val focusManager = LocalFocusManager.current
   val keyboardController = LocalSoftwareKeyboardController.current
   val focusRequester = remember { FocusRequester() }

   val arrowSize = 24.dp
   var textHeight by remember { mutableStateOf(0.dp) }
   var boxWidth by remember { mutableStateOf(0.dp) }
   var alphaBasicFieldText by remember { mutableFloatStateOf(0F) }
   val arrowsPadding = 8.dp

   val coroutineScope = rememberCoroutineScope()
   val lazyListState = rememberLazyListState(initialIdx)
   val snapBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
   val layoutInfo by remember {
      derivedStateOf { lazyListState.layoutInfo }
   }

   var currentIndex by rememberSaveable(initialIdx) { mutableIntStateOf(initialIdx) }
   val selectedIndex by remember {
      derivedStateOf {
         if (wrapUpChoices) currentIndex.mod(behavior.size) else currentIndex
      }
   }
   val padding by remember {
      derivedStateOf {
         val viewPortSize = with(density) { layoutInfo.viewportSize.height.toDp() }
         val res = (viewPortSize - textHeight) / 2
         max(0.dp.value, res.value).dp
      }
   }
   var basicFieldTextValue by remember(currentIndex) {
      mutableStateOf("")
   }
   var labelText by remember(currentIndex){
      mutableStateOf(behavior.getText(selectedIndex) ?: "")
   }

   val onVerticalDrag: (PointerInputChange, Float) -> Unit = { _, amount ->
      if (enableScrolling && alphaBasicFieldText == 0F) {
         coroutineScope.launch {
            lazyListState.scrollBy(-amount)
         }
      }
   }

   val onDragEnd: () -> Unit = {
      if (enableScrolling && alphaBasicFieldText == 0F) {
         coroutineScope.launch {
            lazyListState.layoutInfo.visibleItemsInfo.minByOrNull {
               val offset = it.offset.toFloat()
               abs(offset + sign(offset) * it.size / 2F)
            }?.let {
               currentIndex = it.index
               lazyListState.animateScrollToItem(currentIndex)
               if(wrapUpChoices)
                  lazyListState.scrollToItem(half + selectedIndex)
               onItemSelection(selectedIndex)
            }
         }

      }
   }

   LaunchedEffect(layoutInfo.viewportSize.height, initialIdx) {
      coroutineScope.launch {
         lazyListState.scrollToItem(initialIdx)
      }
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
            .align(Alignment.Center)
            .focusRequester(focusRequester)
            .focusable()
      ) {
         items(
            count = if (wrapUpChoices || behavior.size == Int.MAX_VALUE) Int.MAX_VALUE else behavior.size,
            key = { it },
         ) { rawIdx ->

            val idx = rawIdx.mod(behavior.size)

            val value = behavior.getText(idx)!!

            val (alpha, scale) = layoutInfo.visibleItemsInfo.find {
               it.index.mod(behavior.size) == idx
            }?.let { item ->
               val distance = abs(item.offset.toFloat())
               if (layoutInfo.viewportSize.height > 0) {
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
               maxLines = 1,
               autoSize = textAutoSize,
               modifier = Modifier
                  .onGloballyPositioned {
                     if (currentIndex.mod(behavior.size) == idx) {
                        textHeight = with(density) { it.size.height.toDp() }
                        boxWidth = with(density) { it.size.width.toDp() }
                     }
                  }
                  .alpha(
                     if (currentIndex.mod(behavior.size) != idx || alphaBasicFieldText == 0F)
                        alpha else 0F
                  )
                  .scale(scale)
            )
         }
      }

      if(textHeight != 0.dp && boxWidth != 0.dp) {

         BasicTextField(
            value = basicFieldTextValue,
            onValueChange = {
               basicFieldTextValue = it
            },
//            onTextLayout = { textLayout ->
//               boxWidth = with(density) { textLayout.size.width.toDp() }
//            },
            maxLines = 1,
            enabled = enableTextFieldInput,
            textStyle = textStyle,
            keyboardOptions = KeyboardOptions(
               keyboardType = KeyboardType.Number,
               imeAction = ImeAction.Done,
               showKeyboardOnFocus = false
            ),
            keyboardActions = KeyboardActions(
               onDone = {

                  with(behavior.getIndexOf(basicFieldTextValue)) {
                     val isElementFound = this in 0..<behavior.size

                     if (isElementFound) {
                        currentIndex = if (wrapUpChoices) half + this else this
                        currentIndex
                     } else {
                        basicFieldTextValue = behavior.getText(selectedIndex)!!
                        null
                     }
                  }?.let {
                     coroutineScope.launch {
                        lazyListState.scrollToItem(currentIndex)
                     }
                     focusManager.clearFocus()
                     focusRequester.requestFocus()
                     onItemSelection(selectedIndex)
                  }
               }
            ),
            decorationBox = {
               if(basicFieldTextValue.isEmpty())
                  Box(
                     contentAlignment = Alignment.Center
                  ) {
                     Text(
                        text = labelText,
                        style = TitleStyle,
                        modifier = Modifier.alpha(0.5F)
                     )
                  }
               else it()
            },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            modifier = Modifier
               .align(Alignment.Center)
               .animateContentSize()
               .padding(horizontal = 2.dp)
               .height(textHeight)
//               .width(boxWidth)
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
      }

      Column {
         Icon(
            Icons.Outlined.KeyboardArrowUp,
            null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5F),
            modifier = Modifier
               .size(arrowSize)
               .clip(CircleShape)
               .clickable {
                  val newIndex =
                     if (currentIndex in 1..<behavior.size) {
                        --currentIndex
                     } else if ((wrapUpChoices && currentIndex in 1..Int.MAX_VALUE)) {
                        (--currentIndex).mod(behavior.size)
                     } else if (wrapUpChoices) {
                        val offset = (currentIndex - 1).mod(behavior.size)
                        currentIndex = half + offset
                        offset
                     } else null

                  newIndex?.let {
                     coroutineScope.launch {
                        if(wrapUpChoices)
                           lazyListState.scrollToItem(currentIndex + 1)
                        lazyListState.animateScrollToItem(currentIndex)
                     }
                     onItemSelection(selectedIndex)
                  }
                  focusManager.clearFocus()
                  focusRequester.requestFocus()
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
                  val newIdx = if (currentIndex in 0..<(behavior.size - 1)) {
                     ++currentIndex
                  } else if (wrapUpChoices) {
                     val offset = (currentIndex + 1).mod(behavior.size)
                     currentIndex = half + offset
                     offset
                  } else {
                     null
                  }
                  newIdx?.let {
                     coroutineScope.launch {
                        if (wrapUpChoices)
                           lazyListState.scrollToItem(currentIndex - 1)
                        lazyListState.animateScrollToItem(currentIndex)
                     }
                     onItemSelection(selectedIndex)
                  }
                  focusManager.clearFocus()
                  focusRequester.requestFocus()
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
}

@Preview
@Composable
fun PreviewWheelPicker() {
   AnyMeTheme {

      Column(
         horizontalAlignment = Alignment.CenterHorizontally,
         verticalArrangement = Arrangement.Center,
         modifier = Modifier.fillMaxSize()
      ) {
         WheelPicker(
            8,
            object: WheelPickerBehavior{
               override val size: Int = Int.MAX_VALUE

               override fun getText(idx: Int): String? =
                  if(idx in 0..<size) "$idx" else null

               override fun getIndexOf(string: String): Int = try {
                     val value = string.toInt()
                  if(value in 0..<size) value else -1
                  } catch (_: NumberFormatException) {
                     -1
                  }
            },
            TitleStyle,
            wrapUpChoices = false
         ) {
//            Log.d("Number Picker", "Item selected of index $it")
         }
      }
   }
}
