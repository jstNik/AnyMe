package com.example.anyme.domain.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.ui.composables.GridEntry
import com.example.anyme.ui.composables.ListEntry

data class MalListGridItem(
   override val id: Int,
   override val title: String,
   override val mainPicture: MainPicture,
   val mean: Double
): MalAnime, ListItem {

   @Composable
   override fun Render(
      modifier: Modifier,
      onClick: (ListItem) -> Unit
   ) {

      GridEntry(
         width = 130.dp,
         overImageContent = {
            Row(
               modifier = Modifier
                  .padding(4.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8F))
                  .padding(4.dp)
            ) {

//                  Image()

               Text(
                  mean.toString()
               )
            }
         },
         modifier = modifier
      )

   }
}