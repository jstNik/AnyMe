package com.example.anyme.ui.composables

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anyme.ui.theme.AnyMeTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
   modifier: Modifier = Modifier,
   textFieldState: TextFieldState = rememberTextFieldState(),
   onTextChange: (String) -> Unit
){

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
      onKeyboardAction = KeyboardActionHandler { /* TODO Close keyboard */ },

      decorator = {
         TextFieldDefaults.DecorationBox(
            value = textFieldState.text.toString(),
            innerTextField = it,
            enabled = true,
            singleLine = true,
            visualTransformation = VisualTransformation.None,
            interactionSource = remember{ MutableInteractionSource() },
            contentPadding = PaddingValues(4.dp),
            placeholder = { Text("Search...") }
         )
      },

      modifier = modifier
   )

}

@Preview
@Composable
fun PreviewSearchBar(){
   AnyMeTheme {
      SearchBar{ }
   }
}