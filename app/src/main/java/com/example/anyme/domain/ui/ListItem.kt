package com.example.anyme.domain.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.anyme.domain.mal_dl.MainPicture

interface ListItem {

   val id: Int
   val title: String
   val mainPicture: MainPicture

   @Composable
   fun Render(
      modifier: Modifier = Modifier,
      onClick: (ListItem) -> Unit
   )

}