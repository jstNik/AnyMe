package com.example.anyme.ui.composables.details

import android.content.res.Configuration
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.domain.ui.MediaListItem
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme

@Composable
fun RelatedMediaCard(
   cardTitle: String,
   medias: List<MediaListItemRender>,
   modifier: Modifier = Modifier,
   colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
   contentPadding: PaddingValues = PaddingValues(),
   content: @Composable (idx: Int, it: MediaListItemRender) -> Unit = { _, _ -> }
) {


   Card(
      colors = colors,
      modifier = modifier
   ) {

      Column(modifier = Modifier.padding(contentPadding)) {

         TitleSection(
            text = cardTitle,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
         )

         LazyRow {
            itemsIndexed(
               items = medias.toTypedArray(),
               key = { idx, it ->
                  if (it.media.id != 0) it.media.id
                  else (-idx - 1)
               }
            ) { idx, it ->
               content(idx, it)
               if(idx != medias.size - 1)
                  Spacer(Modifier.width(8.dp))
            }
         }
      }
   }


}

@Preview(showBackground = false,
   uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewRelatedMediaCard(){

   val media = getMediaPreview()
   val related = media.mapToMalAnimeDetails().relatedAnime
   AnyMeTheme {
      RelatedMediaCard(
         cardTitle = "Related Anime",
         medias = related,
         contentPadding = PaddingValues(8.dp),
      ) { idx, it ->
         it.Compose {  }
      }
   }

}