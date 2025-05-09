package com.example.anyme.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.ui.ListItem

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun ListEntry(
   item: ListItem,
   modifier: Modifier = Modifier,
   onClick: (ListItem) -> Unit,
   content: @Composable ColumnScope.() -> Unit
) {

   Card(
      onClick = { onClick(item) },
      modifier = Modifier
         .fillMaxWidth()
         .height(128.dp)
         .background(MaterialTheme.colorScheme.surfaceContainer)
         .then(modifier)
   ) {
      Row {

         BlurredGlideImage(
            model = item.mainPicture.medium,
            contentDescription = "Anime image",
            minRatio = 0.4F,
            maxRatio = 1F,
            blur = 16.dp,
            modifier = Modifier.width(90.dp).fillMaxHeight()
         )

         Column(
            modifier = Modifier
               .padding(8.dp)
               .fillMaxWidth()
         ) {

            Text(
               item.title,
               color = MaterialTheme.colorScheme.primary,
               fontWeight = FontWeight.Bold
            )

            content()
         }
      }
   }
}