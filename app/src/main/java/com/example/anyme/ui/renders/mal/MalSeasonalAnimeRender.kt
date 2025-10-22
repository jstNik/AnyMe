package com.example.anyme.ui.renders.mal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.mapToMalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.ui.composables.ListEntry
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme

class MalSeasonalAnimeRender(
   override val media: MalSeasonalListItem = MalSeasonalListItem()
): MediaListItemRender {

   @Composable
   override fun Compose(
      onClick: () -> Unit
   ) {
      with(media) {

         ListEntry(
            media = media,
            imageHeight = 128.dp,
            contentPadding = PaddingValues(8.dp)
         ) {

         }
      }
   }

}

@Preview
@Composable
fun PreviewMalSeasonalAnimeRender(){

   val media = getMediaPreview()

   AnyMeTheme {
      MalSeasonalAnimeRender(media.mapToMalSeasonalListItem()).Compose {

      }
   }

}