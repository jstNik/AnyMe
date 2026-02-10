package com.example.anyme.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.mapToMalAnimeListItem
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.IMAGE_IDEAL_RATIO

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListEntry(
   media: Media,
   imageHeight: Dp,
   contentPadding: PaddingValues,
   modifier: Modifier = Modifier,
   colors: CardColors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
   imageIdealRatio: Double = IMAGE_IDEAL_RATIO,
   cornerSize: Dp = 16.dp,
   titleAutoSize: TextAutoSize? = null,
   titleStyle: TextStyle = LocalTextStyle.current,
   maxLines: Int = 2,
   onClick: () -> Unit = { },
   overImageContent: @Composable BoxScope.() -> Unit = { },
   content: @Composable ColumnScope.() -> Unit
) {

   Card(
      colors = colors,
      onClick = onClick,
      shape = RoundedCornerShape(cornerSize),
      modifier = Modifier
         .fillMaxWidth()
         .height(imageHeight)
         .then(modifier)
   ) {

      Row(
         modifier = Modifier
         .fillMaxSize()
         .background(Color.Transparent)
      ) {
         Box(
            modifier = Modifier
               .width((imageHeight.value * imageIdealRatio).dp)
               .height(imageHeight)
               .clip(
                  RoundedCornerShape(
                     cornerSize, 0.dp, 0.dp, cornerSize
                  )
               )
         ) {
            BlurredGlideImage(
               model = media.mainPicture.medium,
               contentDescription = "Anime image",
               minRatio = 0.4F,
               maxRatio = 1F,
               blur = 16.dp
            )

            overImageContent()

         }

         Column(
            modifier = Modifier
               .fillMaxWidth()
               .padding(contentPadding)
         ) {

            Text(
               media.title,
               style = titleStyle,
               maxLines = maxLines,
               autoSize = titleAutoSize,
               overflow = TextOverflow.Ellipsis
            )
            content()
         }
      }
   }
}


@Preview
@Composable
fun PreviewListEntry(){

   val media = getMediaPreview()

   val render = object : MediaListItemRender {

      override val media: MalUserListItem = media.mapToMalAnimeListItem()

      @Composable
      override fun Compose(
         onClick: () -> Unit
      ) {
         ListEntry(
            contentPadding = PaddingValues(),
            media = media,
            imageHeight = 128.dp,
         ) {

         }
      }
   }

   AnyMeTheme {
      render.Compose {

      }
   }
}