package com.example.anyme.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.placeholder
import com.example.anyme.R
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.ui.ListItem
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.IMAGE_IDEAL_RATIO

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListItem.ListEntry(
   imageHeight: Dp,
   modifier: Modifier = Modifier,
   imageIdealRatio: Double = IMAGE_IDEAL_RATIO,
   roundedCornerSize: Dp = 16.dp,
   onClick: (ListItem) -> Unit = { },
   overImageContent: @Composable BoxScope.() -> Unit = { },
   content: @Composable ColumnScope.() -> Unit
) {

   Card(
      onClick = { onClick(this) },
      shape = RoundedCornerShape(roundedCornerSize),
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
                     roundedCornerSize, 0.dp, 0.dp, roundedCornerSize
                  )
               )
         ) {
            BlurredGlideImage(
               model = mainPicture.medium,
               contentDescription = "Anime image",
               failure = placeholder(R.drawable.main_picture),
               minRatio = 0.4F,
               maxRatio = 1F,
               blur = 16.dp
            )

            overImageContent()

         }

         Column(
            modifier = Modifier.fillMaxWidth()
         ) {

            Text(
               title,
               color = MaterialTheme.colorScheme.primary,
               fontWeight = FontWeight.Bold,
               maxLines = 2,
               overflow = TextOverflow.Ellipsis,
               modifier = Modifier.fillMaxWidth()
            )
            content()
         }
      }
   }
}


@Preview
@Composable
fun PreviewListEntry(){
   val item = object : ListItem {
      override val id = 1
      override val mainPicture = MainPicture()
      override val title = "Hello world"

      @Composable
      override fun Render(modifier: Modifier, onClick: (ListItem) -> Unit) {
         ListEntry(
            imageHeight = 128.dp,
         ) {

         }
      }
   }

   AnyMeTheme {
      item.Render(Modifier) { }
   }
}