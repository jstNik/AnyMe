package com.example.anyme.ui.renders.mal

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.mapToMalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.ui.composables.ListEntry
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.demographics
import com.example.anyme.ui.theme.realGenres
import com.example.anyme.ui.theme.stepBased
import com.example.anyme.ui.theme.themes
import kotlin.math.abs
import kotlin.math.min

class MalSeasonalAnimeRender(
   override val media: MalSeasonalListItem = MalSeasonalListItem()
): MediaListItemRender {

   @Composable
   override fun Compose(
      onClick: () -> Unit
   ) {
      with(media) {
         val typo = MaterialTheme.typography
         val cs = MaterialTheme.colorScheme
         val indexToTrim by remember {
            derivedStateOf {
               synopsis
                  .mapIndexed { i, c ->
                     if (c == '.') i else null
                  }.filterNotNull().minByOrNull {
                     abs(150 - it)
                  }
            }
         }
         val genresToDisplay by remember {
            derivedStateOf {
               if(genres.size < 4) return@derivedStateOf genres

               val list = genres.toMutableList()

               var gen = list.firstOrNull { it.id in realGenres }
               if(gen == null)
                  gen = list.firstOrNull { it.id in themes }
               if(gen == null)
                  gen = list.firstOrNull { it.id in demographics }
               gen?.let{ list.remove(gen) }

               var dem = list.firstOrNull { it.id in demographics }
               if(dem == null)
                  dem = list.firstOrNull { it.id in themes }
               if(dem == null)
                  dem = list.firstOrNull { it.id in realGenres }
               dem?.let{ list.remove(dem) }

               var theme = list.firstOrNull { it.id in themes }
               if(theme == null)
                  theme = list.firstOrNull { it.id in realGenres }
               if(theme == null)
                  theme = list.firstOrNull { it.id in demographics }
               listOfNotNull(gen, dem, theme)
            }
         }

         ListEntry(
            media = media,
            imageHeight = 128.dp,
            contentPadding = PaddingValues(8.dp),
            onClick = onClick,
            maxLines = 1,
            titleAutoSize = typo.titleMedium.fontSize.stepBased(0.8),
            titleStyle = typo.titleMedium.copy(color = cs.primary),
            overImageContent = {
               Box(modifier = Modifier.fillMaxSize()) {
                  Row(
                     modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(cs.surfaceContainerHighest.copy(alpha = 0.8F))
                        .padding(4.dp)
                  ) {
                     Text(
                        text = "%.02f".format(mean),
                        style = typo.bodyMedium.copy(
                           color = cs.primary,
                           fontWeight = FontWeight.SemiBold
                        )
                     )
                  }

                  if(listStatus != MyList.Status.Unknown)
                     Row(
                        modifier = Modifier
                           .align(Alignment.BottomEnd)
                           .padding(4.dp)
                           .clip(RoundedCornerShape(8.dp))
                           .background(cs.surfaceContainerHighest.copy(alpha = 0.8F))
                           .padding(4.dp)
                     ) {
                        Icon(
                           Icons.AutoMirrored.Filled.PlaylistAddCheck,
                           null,
                           tint = cs.primary
                        )
                     }

               }
            }
         ) {

            if(genresToDisplay.isNotEmpty()) {
               Spacer(modifier = Modifier.padding(top = 4.dp))
               Row {
                  genresToDisplay.forEachIndexed { idx, genre ->
                     Text(
                        text = genre.name,
                        style = typo.bodySmall.copy(color = cs.tertiary),
                        modifier = Modifier
                           .clip(RoundedCornerShape(percent = 100))
                           .background(cs.onTertiary)
                           .padding(vertical = 4.dp, horizontal = 8.dp)
                     )
                     if (idx != genresToDisplay.size - 1)
                        Spacer(modifier = Modifier.width(4.dp))
                  }
               }
            }

            Spacer(modifier = Modifier.padding(top = 4.dp))

            indexToTrim?.let {
               Text(
                  text = synopsis.substring(0..it).replace(
                     Regex("[\\t\\r\\n]"), " "
                  ),
                  style = typo.bodyMedium.copy(
                     lineHeight = (typo.bodyMedium.lineHeight.value / typo.bodyMedium.fontSize.value).em
                  ),
                  maxLines = 3,
                  overflow = TextOverflow.Ellipsis,
                  autoSize = TextAutoSize.StepBased(
                     minFontSize = typo.bodyMedium.fontSize * 0.8,
                     maxFontSize = typo.bodyMedium.fontSize
                  )
               )
            }
         }
      }
   }

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewMalSeasonalAnimeRender(){

   val media = getMediaPreview()

   AnyMeTheme {
      MalSeasonalAnimeRender(media.mapToMalSeasonalListItem()).Compose {
      }
   }

}