package com.example.anyme.ui.renders.mal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.remote.mal.Ranking
import com.example.anyme.domain.remote.mal.mapToMalRankingListItem
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.example.anyme.ui.composables.GridEntry
import com.example.anyme.ui.composables.GridItemOverImage
import com.example.anyme.ui.composables.GridTitle
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.cs
import com.example.anyme.ui.theme.stepBased
import com.example.anyme.ui.theme.typo

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
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainerHighest),
            contentPadding = 16.dp,
            media = media,
            pictureWidth = 130.dp,
            cornerSize = 24.dp,
            verticalArrangement = Arrangement.SpaceEvenly,
            onClick = onClick,
            overImageContent = {
               GridItemOverImage(
                  mean = mean,
                  listStatus = listStatus,
                  parentCornerSize = it
               )
            }
         ) {

            Text(
               text = "$rank°",
               style = typo.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  color = cs.primary,
                  lineHeight = (typo.titleMedium.lineHeight.value / typo.titleMedium.fontSize.value).em
               ),
               maxLines = 1,
               autoSize = typo.titleMedium.fontSize.stepBased(0.5),
               overflow = TextOverflow.Ellipsis
            )

            GridTitle(title)

         }
      }
   }

}

@Preview
@Composable
fun PreviewMalRankingFrameRender(){

   val media = getMediaPreview()
   val data = Data(media, Ranking(Int.MAX_VALUE))
   AnyMeTheme {
      Column(
         modifier = Modifier.height(300.dp)
      ) {
         MalRankingFrameRender(data.mapToMalRankingListItem()).Compose {
         }
      }
   }

}
