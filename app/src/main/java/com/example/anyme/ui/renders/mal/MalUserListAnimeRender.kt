package com.example.anyme.ui.renders.mal

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.mapToMalAnimeListItem
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.ui.composables.ListEntry
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme

class MalUserListAnimeRender(
   override val media: MalUserListItem = MalUserListItem()
): MediaListItemRender {

   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Compose(
      onClick: () -> Unit
   ) {
      with(media) {
         ListEntry(
            media = media,
            imageHeight = 128.dp,
            contentPadding = PaddingValues(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            onClick = onClick

         ) {
            Spacer(Modifier.weight(1F))

            Row(
               horizontalArrangement = Arrangement.End,
               modifier = Modifier
                  .padding(10.dp)
                  .fillMaxWidth()
            ) {
               Text(
                  "$myListStatusNumEpisodesWatched / $numEpisodes",
                  color = MaterialTheme.colorScheme.primary
               )
            }
            Box(
               Modifier.padding()
            ) {
               LinearProgressIndicator(
                  progress = {
                     if (nextEp.number > 0 && numEpisodes == 0)
                        1F
                     else if (nextEp.number > 0 && numEpisodes > 0 && nextEp.number <= numEpisodes)
                        nextEp.number / numEpisodes.toFloat()
                     else 0F
                  },
                  drawStopIndicator = { },
                  modifier = Modifier
                     .fillMaxWidth()
                     .height(4.dp),
                  color = MaterialTheme.colorScheme.tertiary,
                  strokeCap = StrokeCap.Round,
                  gapSize = 0.dp
               )

               LinearProgressIndicator(
                  progress = {
                     if (numEpisodes > 0)
                        myListStatusNumEpisodesWatched / numEpisodes.toFloat()
                     else 0F
                  },
                  drawStopIndicator = { },
                  modifier = Modifier
                     .fillMaxWidth()
                     .height(4.dp),
                  color = MaterialTheme.colorScheme.primary,
                  trackColor = Color.Transparent,
                  strokeCap = StrokeCap.Round,
                  gapSize = 0.dp
               )
            }
         }
      }
   }

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewMalUserListItem() {

   val media = getMediaPreview()

   AnyMeTheme {
      MalUserListAnimeRender(media.mapToMalAnimeListItem()).Compose {

      }
   }

}