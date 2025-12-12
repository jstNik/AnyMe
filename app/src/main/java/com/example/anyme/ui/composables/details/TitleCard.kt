package com.example.anyme.ui.composables.details

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.anyme.R
import com.example.anyme.ui.composables.BlurredGlideImage
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.theme.Debug
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.TitleStyle

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun TitleCard(
   title: String,
   leftStat: Pair<String, Number>,
   rightStat: Pair<String, Number>,
   mainPicture: Any,
   backgroundPicture: Any,
   pictureHeight: Dp,
   pictureWidth: Dp,
   yPictureOffset: Dp,
   debug: Boolean,
   modifier: Modifier = Modifier,
   alternativeTitleTextAutoSize: TextAutoSize? = null,
   titleTextAutoSize: TextAutoSize? = null,
   alternativeTitle: String? = null,
   colors: CardColors = CardDefaults.cardColors(),
   contentPadding: PaddingValues = PaddingValues(),
   bottomContent: @Composable RowScope.() -> Unit
) {

   val cs = MaterialTheme.colorScheme
   val typo = MaterialTheme.typography

   val layoutDirection = LocalLayoutDirection.current
   val startPad = contentPadding.calculateLeftPadding(layoutDirection)
   val endPad = contentPadding.calculateRightPadding(layoutDirection)

   Box(modifier = Modifier.fillMaxWidth()) {

      if (!debug) {
         GlideImage(
            model = backgroundPicture,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
               .fillMaxWidth()
               .height(200.dp)
               .drawWithContent {
                  drawContent()
                  val brush = linearGradient(
                     listOf(Color.Transparent, cs.background),
                     end = Offset(0F, size.height * 0.75F)
                  )
                  drawRect(brush)
               }
         )
      } else {
         Image(
            painterResource(R.drawable.placeholder_1920x1080),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
               .fillMaxWidth()
               .height(200.dp)
               .drawWithContent {
                  drawContent()
                  val brush = linearGradient(
                     listOf(Color.Transparent, cs.background),
                     end = Offset(0F, size.height * 0.75F)
                  )
                  drawRect(brush)
               }
         )
      }

      Card(
         colors = colors,
         modifier = Modifier
            .padding(top = 170.dp)
            .then(modifier)
      ) {
         Box {
            Row(
               modifier = Modifier
                  .fillMaxWidth()
                  .padding(top = contentPadding.calculateTopPadding())
            ) {
               Spacer(modifier = Modifier.width(pictureWidth))
               Spacer(modifier = Modifier.weight(1F))

               Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier.padding(start = startPad)
               ) {
                  Text(
                     text = leftStat.first,
                     style = TitleStyle
                  )
                  Text(
                     text = leftStat.second.toString(),
                     style = TitleStyle
                  )
               }


               Spacer(modifier = Modifier.weight(2F))

               Column(
                  horizontalAlignment = Alignment.CenterHorizontally
               ) {
                  Text(
                     text = rightStat.first,
                     style = TitleStyle
                  )
                  Row {
                     Text(
                        text = "#",
                        style = typo.bodyMedium,
                        color = cs.secondary,
                        modifier = Modifier
                           .align(Alignment.Bottom)
                     )
                     Text(
                        text = "${rightStat.second}",
                        style = TitleStyle
                     )
                  }
               }
               Spacer(modifier = Modifier.weight(1F))
            }

            Row(
               verticalAlignment = Alignment.CenterVertically,
               modifier = Modifier
                  .padding(top = yPictureOffset)
                  .padding(contentPadding)
            ) {
               bottomContent()
            }
         }
      }


      Row(
         modifier = Modifier
            .graphicsLayer {
               translationY = yPictureOffset.toPx()
            }.padding(
               start = startPad,
               end = endPad
            ).then(modifier)
      ) {
         if(!debug) {
            BlurredGlideImage(
               model = mainPicture,
               contentDescription = null,
               contentScale = ContentScale.Crop,
               modifier = Modifier
                  .width(pictureWidth)
                  .height(pictureHeight)
                  .clip(RoundedCornerShape(8.dp))
            )
         } else {
            Image(
               painter = painterResource(R.drawable.main_picture),
               contentDescription = null,
               contentScale = ContentScale.Crop,
               modifier = Modifier
                  .width(pictureWidth)
                  .height(pictureHeight)
                  .clip(RoundedCornerShape(8.dp))
            )
         }

         Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
               .height(pictureHeight - yPictureOffset)
               .padding(contentPadding)
         ) {
            Text(
               title,
               style = typo.headlineMedium.copy(
                  lineHeight = (typo.headlineMedium.lineHeight.value / typo.headlineMedium.fontSize.value).em,
                  fontWeight = FontWeight.Bold
               ),
               color = cs.primary,
               maxLines = 2,
               autoSize = titleTextAutoSize
            )
            alternativeTitle?.let {
               if (it.isNotBlank())
                  Text(
                     text = it,
                     color = cs.secondary,
                     style = typo.titleLarge.copy(
                        lineHeight = (typo.titleLarge.lineHeight.value / typo.titleLarge.fontSize.value).em
                     ),
                     maxLines = 2,
                     autoSize = alternativeTitleTextAutoSize
                  )
            }
         }
      }

   }
}


@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewTitleCard() {

   val media = getMediaPreview()

   AnyMeTheme {
      TitleCard(
         title = media.title,
         leftStat = "Score" to media.mean,
         rightStat = "Rank" to media.rank,
         mainPicture = media.mainPicture,
         pictureWidth = 120.dp,
         pictureHeight = 170.dp,
         yPictureOffset = 90.dp,
         backgroundPicture = "",
         debug = Debug,
         modifier = Modifier.padding(horizontal = 16.dp),
         alternativeTitle = media.alternativeTitles.en,
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
         contentPadding = PaddingValues(8.dp),
      ) { }
   }

}