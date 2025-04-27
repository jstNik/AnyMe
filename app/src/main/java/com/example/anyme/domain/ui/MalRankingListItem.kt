package com.example.anyme.domain.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.ui.composables.BlurredGlideImage

data class MalRankingListItem(
   override val id: Int = 0,
   override val title: String = "",
   val picture: String = "",
   val rank: Int = 0,
) : MalAnime, ListItem {


   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Render(
      modifier: Modifier,
      onClick: (ListItem) -> Unit
   ) {
      Card(
         onClick = { onClick(this) },
         shape = RoundedCornerShape(16.dp),
         colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
         modifier = Modifier
            .then(modifier)
            .width(130.dp)
            .height(240.dp)
      ) {

         Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
         ) {
            Box(
               modifier = Modifier
                  .fillMaxWidth()
                  .height(180.dp)
                  .padding(8.dp, 8.dp, 8.dp, 0.dp)
                  .clip(RoundedCornerShape(16.dp))
            ){

               BlurredGlideImage(
                  picture,
                  null,
                  minRatio = 0.4F,
                  maxRatio = 1F,
                  blur = 16.dp,
               )

               Row(
                  modifier = Modifier
                     .padding(4.dp)
                     .clip(RoundedCornerShape(8.dp))
                     .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8F))
                     .padding(4.dp)
               ) {

//                  Image()

                  Text(
                     rank.toString()
                  )
               }
            }

            Spacer(modifier = Modifier.weight(0.5F))

            Text(
               title,
               maxLines = 2,
               overflow = TextOverflow.Ellipsis,
               style = MaterialTheme.typography.bodyLarge,
               textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.weight(0.5F))
         }
      }
   }
}