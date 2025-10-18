package com.example.anyme.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.R
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.mapToMalListGridItem
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.IMAGE_IDEAL_RATIO

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GridEntry(
   media: Media,
   contentPadding: PaddingValues,
   belowImageContentHeight: Dp,
   modifier: Modifier = Modifier,
   width: Dp = 130.dp,
   imageIdealRatio: Double = IMAGE_IDEAL_RATIO,
   roundedCornerSize: Dp = 16.dp,
   horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
   verticalArrangement: Arrangement.Vertical = Arrangement.SpaceAround,
   colors: CardColors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
   debug: Boolean = false,
   onClick: (Media) -> Unit = { },
   overImageContent: @Composable BoxScope.() -> Unit = { },
   content: @Composable ColumnScope.() -> Unit
) {

   val shape = remember { RoundedCornerShape(roundedCornerSize) }

   Card(
      onClick = { onClick(media) },
      shape = shape,
      colors = colors,
      modifier = modifier
   ) {

      val height = (width.value / imageIdealRatio).dp

      Column(
         modifier = Modifier
            .height(height + belowImageContentHeight)
            .padding(contentPadding)
      ) {

         Box(
            modifier = Modifier
               .height((width.value / imageIdealRatio).dp)
               .clip(shape)
         ) {

            if(!debug) {
               BlurredGlideImage(
                  media.mainPicture.medium,
                  null,
                  minRatio = 0.4F,
                  maxRatio = 1F,
                  blur = 16.dp,
                  modifier = Modifier.width(width)
               )
            } else {
               Image(
                  painterResource(R.drawable.main_picture),
                  null,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.width(width)
               )
            }

            overImageContent()

         }

         Column(
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            modifier = Modifier
               .width(width)
               .fillMaxHeight()
         ) {

            content()
         }
      }
   }

}

@Composable
fun GridEntry(
   media: Media,
   contentPadding: PaddingValues,
   belowImageContentHeight: Dp,
   modifier: Modifier = Modifier,
   width: Dp = 130.dp,
   imageIdealRatio: Double = IMAGE_IDEAL_RATIO,
   roundedCornerSize: Dp = 16.dp,
   horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
   verticalArrangement: Arrangement.Vertical = Arrangement.SpaceAround,
   colors: CardColors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
   onClick: (Media) -> Unit = { },
   overImageContent: @Composable BoxScope.() -> Unit = { },
   content: @Composable ColumnScope.() -> Unit
) {
   GridEntry(
      media = media,
      contentPadding = contentPadding,
      belowImageContentHeight = belowImageContentHeight,
      modifier = modifier,
      width = width,
      imageIdealRatio = imageIdealRatio,
      roundedCornerSize = roundedCornerSize,
      horizontalAlignment = horizontalAlignment,
      verticalArrangement = verticalArrangement,
      colors = colors,
      onClick = onClick,
      overImageContent = overImageContent,
      content = content,
      debug = false
   )
}


@Preview
@Composable
private fun PreviewGridItem() {

   val media = getMediaPreview()
   val render = object: MediaListItemRender{
      override val media: MalListGridItem = media.mapToMalListGridItem()

      @Composable
      override fun Compose(onClick: (Media) -> Unit) {
         GridEntry(
            width = 130.dp,
            media = media,
            belowImageContentHeight = 50.dp,
            contentPadding = PaddingValues(),
            overImageContent = {
               Row(
                  modifier = Modifier
                     .padding(4.dp)
                     .clip(RoundedCornerShape(8.dp))
                     .background(MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.8F))
                     .padding(4.dp)
               ) {

//                  Image()

                  Text(
                     "213"
                  )
               }
            }
         ){
            Text("Hello World")
         }

      }

   }

   AnyMeTheme {
      render.Compose(
      ) { }
   }

}

