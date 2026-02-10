package com.example.anyme.ui.renders.mal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anyme.domain.dl.mal.mapToMalListGridItem
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.ui.composables.GridEntry
import com.example.anyme.ui.composables.GridItemOverImage
import com.example.anyme.ui.composables.GridTitle
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
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
            contentPadding = 16.dp,
            cornerSize = 32.dp,
            media = media,
            pictureWidth = 150.dp,
            verticalArrangement = Arrangement.SpaceEvenly,
            onClick = onClick,
            modifier = Modifier.fillMaxHeight(),
            overImageContent = {
               GridItemOverImage(
                  mean = mean,
                  listStatus = listStatus,
                  parentCornerSize = it
               )
            }
         ) {

            GridTitle(title)

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