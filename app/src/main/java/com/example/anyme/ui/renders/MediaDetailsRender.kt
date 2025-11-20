package com.example.anyme.ui.renders

import androidx.compose.runtime.Composable
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.domain.dl.Media

interface MediaDetailsRender {

   val media: Media

   @Composable
   fun Compose()

}