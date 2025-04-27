package com.example.anyme.domain.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

interface ListItem {

   val id: Int
   val title: String

   @Composable
   fun Render(
      modifier: Modifier,
      onClick: (ListItem) -> Unit
   )

}