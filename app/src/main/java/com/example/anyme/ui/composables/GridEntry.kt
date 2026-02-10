package com.example.anyme.ui.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.R
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.mapToMalListGridItem
import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.IMAGE_IDEAL_RATIO
import com.example.anyme.ui.theme.cs
import com.example.anyme.ui.theme.debug
import com.example.anyme.ui.theme.stepBased
import com.example.anyme.ui.theme.typo
import kotlin.math.max

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GridEntry(
   media: Media,
   contentPadding: Dp,
   modifier: Modifier = Modifier,
   pictureWidth: Dp = 130.dp,
   imageIdealRatio: Double = IMAGE_IDEAL_RATIO,
   cornerSize: Dp = 0.dp,
   horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
   verticalArrangement: Arrangement.Vertical = Arrangement.SpaceAround,
   colors: CardColors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
   onClick: () -> Unit = { },
   overImageContent: @Composable BoxScope.(Dp) -> Unit = { },
   content: @Composable ColumnScope.() -> Unit
) {

   val height = (pictureWidth.value / imageIdealRatio).dp
   val parentShape = remember { RoundedCornerShape(cornerSize) }
   val childCornerSize = (cornerSize.value - contentPadding.value).coerceAtLeast(0F).dp
   val childShape = remember { RoundedCornerShape(childCornerSize) }

   Card(
      onClick = onClick,
      shape = parentShape,
      colors = colors,
      modifier = modifier
   ) {

      Column(
         modifier = Modifier
            .padding(start = contentPadding, top = contentPadding, end = contentPadding)
      ) {

         Box(
            modifier = Modifier
               .width(pictureWidth)
               .height((pictureWidth.value / imageIdealRatio).dp)
               .clip(childShape)
         ) {

            if(!debug) {
               BlurredGlideImage(
                  media.mainPicture.medium,
                  null,
                  minRatio = 0.4F,
                  maxRatio = 1F,
                  blur = 16.dp
               )
            } else {
               Image(
                  painterResource(R.drawable.main_picture),
                  null,
                  alignment = Alignment.Center,
                  contentScale = ContentScale.Crop,
                  modifier = Modifier.fillMaxSize()
               )
            }

            overImageContent(childCornerSize)

         }

         Column(
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
            modifier = Modifier
               .width(pictureWidth)
               .fillMaxHeight()
         ) {
            content()
         }
      }
   }

}

@Composable
fun BoxScope.GridItemOverImage(
   mean: Double,
   listStatus: MyList.Status,
   parentCornerSize: Dp = 0.dp
) {

   val padding = 4.dp
   val cornerSize = (parentCornerSize.value - padding.value).coerceAtLeast(0F).dp
   val shape = remember{RoundedCornerShape(cornerSize)}

//   Row(
//      modifier = Modifier
//         .align(Alignment.TopStart)
//         .padding(4.dp)
//         .clip(shape)
//         .background(cs.surfaceContainerHighest.copy(alpha = 0.8F))
//         .padding(4.dp)
//   ) {
      Text(
         text = "%.02f".format(mean),
         style = typo.bodyMedium.copy(
            color = cs.primary,
            fontWeight = FontWeight.SemiBold
         ),
         modifier = Modifier
            .align(Alignment.TopStart)
            .padding(4.dp)
            .clip(shape)
            .background(cs.surfaceContainerHighest.copy(alpha = 0.8F))
            .padding(4.dp)
      )
//   }

   if (listStatus != MyList.Status.Unknown)
      Row(
         modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(4.dp)
            .clip(shape)
            .background(cs.surfaceContainerHighest.copy(alpha = 0.9F))
            .padding(4.dp)
      ) {
         Icon(
            Icons.AutoMirrored.Filled.PlaylistAddCheck,
            null,
            tint = cs.primary
         )
      }
}

@Composable
fun GridTitle(
   title: String,
   modifier: Modifier = Modifier
){
   Text(
      text = title,
      style = typo.titleMedium.copy(
         fontWeight = FontWeight.Bold,
         color = cs.primary,
         lineHeight = (typo.titleMedium.lineHeight.value / typo.titleMedium.fontSize.value).em,
         textAlign = TextAlign.Center
      ),
      maxLines = 2,
      autoSize = typo.titleMedium.fontSize.stepBased(0.75),
      overflow = TextOverflow.Ellipsis,
      modifier = modifier
   )
}

@Preview
@Composable
private fun PreviewGridItem() {

   val media = getMediaPreview()
   val render = object: MediaListItemRender{
      override val media: MalListGridItem = media.mapToMalListGridItem()

      @Composable
      override fun Compose(onClick: () -> Unit) {
         GridEntry(
            pictureWidth = 130.dp,
            media = media,
            contentPadding = 8.dp,
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

