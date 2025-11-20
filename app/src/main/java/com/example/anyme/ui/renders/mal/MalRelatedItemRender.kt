package com.example.anyme.ui.renders.mal

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.composables.GridEntry
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.theme.Debug
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme
class MalRelatedItemRender(
   override val media: MalAnimeDetails.MalRelatedAnime = MalAnimeDetails.MalRelatedAnime()
) : MediaListItemRender {

   @Composable
   override fun Compose(
      onClick: () -> Unit
   ) {
      with(media) {

         val cs = MaterialTheme.colorScheme
         val typo = MaterialTheme.typography

         val belowImageContentHeight = if(relationTypeFormatted != null)
            90.dp else 50.dp

         GridEntry(
            media = media,
            width = 150.dp,
            contentPadding = PaddingValues(8.dp),
            belowImageContentHeight = belowImageContentHeight,
            debug = Debug,
            colors = CardDefaults.cardColors(containerColor = cs.surfaceContainerHighest),
            onClick = onClick
         ) {
            Column(
               horizontalAlignment = Alignment.CenterHorizontally,
               verticalArrangement = Arrangement.SpaceEvenly,
               modifier = Modifier.padding(top = 8.dp)
            ) {

               Text(
                  text = title,
                  style = typo.titleSmall,
                  color = cs.secondary,
                  textAlign = TextAlign.Center,
                  fontWeight = FontWeight.Bold,
                  maxLines = 2,
                  overflow = TextOverflow.Ellipsis
               )

               relationTypeFormatted?.let { relation ->
                  Text(
                     text = relation,
                     style = typo.bodyMedium,
                     color = cs.secondary,
                  )
               }
            }
         }
      }
   }

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewMediaDetailsScreen() {

   val media = getMediaPreview()

   AnyMeTheme {
      media.mapToMalAnimeDetails().relatedAnime.getOrNull(0)?.let {
         MalRelatedItemRender(it).Compose {  }
      }
   }

}