package com.example.anyme.ui.renders

import androidx.compose.runtime.Composable
import com.example.anyme.ui.navigation.Screen

interface MediaDetailsRender: MediaRender {

   @Composable
   fun Compose(
      onNavigation: (Screen) -> Unit
   )

}