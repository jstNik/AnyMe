package com.example.anyme.domain.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.ui.composables.BlurredGlideImage

interface ListItem {

   val id: Int
   val title: String
   val mainPicture: MainPicture

   @Composable
   fun Render(
      modifier: Modifier,
      onClick: (ListItem) -> Unit
   )




}