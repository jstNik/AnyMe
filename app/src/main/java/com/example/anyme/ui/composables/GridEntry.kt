package com.example.anyme.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.placeholder
import com.example.anyme.BuildConfig
import com.example.anyme.R
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.ui.ListItem
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.IMAGE_IDEAL_RATIO
import kotlin.math.round

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListItem.GridEntry(
   width: Dp,
   modifier: Modifier = Modifier,
   imageIdealRatio: Double = IMAGE_IDEAL_RATIO,
   roundedCornerSize: Dp = 16.dp,
   onClick: (ListItem) -> Unit = { },
   overImageContent: @Composable BoxScope.() -> Unit = { }
) {

   val shape = remember{ RoundedCornerShape(roundedCornerSize) }

   Card(
      onClick = { onClick(this) },
      shape = shape,
      colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer),
      modifier = modifier
   ) {

      Column(
         horizontalAlignment = Alignment.CenterHorizontally,
         modifier = Modifier.fillMaxSize()
      ) {
         Box(
            modifier = Modifier
               .fillMaxWidth()
               .height((width.value / imageIdealRatio).dp)
               .padding(8.dp, 8.dp, 8.dp, 0.dp)
               .clip(shape)
         ) {


            BlurredGlideImage(
               mainPicture.medium,
               null,
               minRatio = 0.4F,
               maxRatio = 1F,
               blur = 16.dp
            )

            overImageContent()

         }


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

@Preview
@Composable
private fun PreviewGridItem() {

   val item = object : ListItem {
      override val id: Int = 1
      override val title: String = "Hello world"
      override val mainPicture: MainPicture =
         MainPicture()

      @Composable
      override fun Render(modifier: Modifier, onClick: (ListItem) -> Unit) {
         GridEntry(
            width = 130.dp,
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
         )
      }

   }

   AnyMeTheme(darkTheme = true) {
      Column(modifier = Modifier.fillMaxSize()) {
         Box(
            Modifier
               .fillMaxWidth()
               .height(240.dp)
         ) {
            item.Render(Modifier) { }
         }
      }
   }

}

