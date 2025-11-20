package com.example.anyme.ui.renders.mal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.mapToMalListGridItem
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.ui.composables.GridEntry
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme

class MalAnimeSearchFrameRender(
   override val media: MalListGridItem = MalListGridItem()
): MediaListItemRender {

   @Composable
   override fun Compose(
      onClick: () -> Unit
   ) {
      with(media) {
         GridEntry(
            media = media,
            contentPadding = PaddingValues(),
            width = 130.dp,
            belowImageContentHeight = 50.dp,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
            onClick = onClick,
            overImageContent = {
               Row(
                  modifier = Modifier
                     .padding(4.dp)
                     .clip(RoundedCornerShape(8.dp))
                     .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8F))
                     .padding(4.dp)
               ) {

                  // TODO Image()

                  Text(
                     mean.toString()
                  )
               }
            },
         ) {

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

@Preview
@Composable
fun PreviewMalAnimeSearchFrameRender(){

   val media = getMediaPreview()
   AnyMeTheme {
      MalAnimeSearchFrameRender(media.mapToMalListGridItem()).Compose {

      }
   }

}