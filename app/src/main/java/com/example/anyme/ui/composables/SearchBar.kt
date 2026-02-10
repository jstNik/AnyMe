package com.example.anyme.ui.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.painter.BrushPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.cs
import com.example.anyme.ui.theme.typo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest

data class AnyMeTextFieldColors(private val unit: Unit = Unit) {
   val containerColor: Color
      @Composable get() = cs.surfaceContainer
   val textColor: Color
      @Composable get () = cs.primary
   val cursorColor: Color
      @Composable get() = textColor
   val leadingIconColor: Color
      @Composable get() = cs.primary.copy(alpha = 0.75F)
   val labelTextColor: Color
      @Composable get() = leadingIconColor
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
   modifier: Modifier = Modifier,
   textFieldState: TextFieldState = rememberTextFieldState(),
   colors: AnyMeTextFieldColors = AnyMeTextFieldColors(),
   textStyle: TextStyle = typo.titleSmall,
   onTextChange: suspend CoroutineScope.(String) -> Unit
){

   val interactionSource = remember { MutableInteractionSource() }

   LaunchedEffect(Unit) {
      snapshotFlow { textFieldState.text.toString() }.collectLatest {
         onTextChange(it)
      }
   }

   BasicTextField(
      textFieldState,
      lineLimits = TextFieldLineLimits.SingleLine,
      keyboardOptions = KeyboardOptions(
         keyboardType = KeyboardType.Text,
         showKeyboardOnFocus = true,
         imeAction = ImeAction.Search
      ),
      textStyle = textStyle.copy(color = colors.textColor),
      cursorBrush = SolidColor(colors.cursorColor),
      decorator = {
         TextFieldDefaults.DecorationBox(
            value = textFieldState.text.toString(),
            leadingIcon = {
               Icon(
                  imageVector = Icons.Filled.Search,
                  contentDescription = null,
                  tint = colors.leadingIconColor
               )
            },
            innerTextField = it,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = interactionSource,
            placeholder = {
               Text(
                  "Search...",
                  color = colors.labelTextColor
               )
            },
            colors = TextFieldDefaults.colors(
               focusedContainerColor = colors.containerColor
            ),
            container = {
               TextFieldDefaults.Container(
                  enabled = true,
                  isError = false,
                  interactionSource = interactionSource,
                  focusedIndicatorLineThickness = 0.dp,
                  unfocusedIndicatorLineThickness = 0.dp,
                  shape = RoundedCornerShape(percent = 100),
               )
            }
         )
      },
      modifier = modifier
   )

}

@Preview
@Composable
fun PreviewSearchBar(){
   AnyMeTheme {
      SearchBar(
         modifier = Modifier.fillMaxWidth()
      ){ }
   }
}