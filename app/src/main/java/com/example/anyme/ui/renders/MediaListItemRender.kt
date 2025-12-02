package com.example.anyme.ui.renders

import androidx.compose.runtime.Composable

interface MediaListItemRender: MediaRender {

   @Composable
   fun Compose(
      onClick: () -> Unit
   )

}