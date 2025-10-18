package com.example.anyme.ui.renders

import androidx.compose.runtime.Composable
import com.example.anyme.domain.dl.Media

interface MediaListItemRender {

   val media: Media

   @Composable
   fun Compose(
      onClick: (Media) -> Unit
   )

}