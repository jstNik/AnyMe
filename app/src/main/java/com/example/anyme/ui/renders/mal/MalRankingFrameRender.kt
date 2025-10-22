package com.example.anyme.ui.renders.mal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.mapToMalListGridItem
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.remote.mal.Ranking
import com.example.anyme.domain.remote.mal.mapToMalRankingListItem
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.example.anyme.ui.composables.GridEntry
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme

class MalRankingFrameRender(
   override val media: MalRankingListItem = MalRankingListItem()
): MediaListItemRender {

   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Compose(
      onClick: () -> Unit
   ) {
      with(media) {
         GridEntry(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            contentPadding = PaddingValues(),
            media = media,
            width = 130.dp,
            belowImageContentHeight = 50.dp,
            overImageContent = {
               Row(
                  modifier = Modifier
                     .padding(4.dp)
                     .clip(RoundedCornerShape(8.dp))
                     .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8F))
                     .padding(4.dp)
               ) {

                  Text(
                     rank.toString()
                  )
               }
            }
         ) { }
      }
   }

}

@Preview
@Composable
fun PreviewMalRankingFrameRender(){

   val media = getMediaPreview()
   val data = Data(media, Ranking(21))
   AnyMeTheme {
      MalRankingFrameRender(data.mapToMalRankingListItem()).Compose {
      }
   }

}
