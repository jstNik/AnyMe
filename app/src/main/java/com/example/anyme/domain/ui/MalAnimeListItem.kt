package com.example.anyme.domain.ui

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.mal_dl.MyListStatus
import com.example.anyme.ui.composables.BlurredGlideImage
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.utils.RangeMap
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@Immutable
data class MalAnimeListItem(
   override val id: Int = 0,
   val title: String = "",
   val mainPictureMedium: String = "",
   val numEpisodes: Int = 0,
   val myListStatusNumEpisodesWatched: Int = 0,
   val myListStatusStatus: MyListStatus.Status = MyListStatus.Status.Undefined,
   val status: MalAnimeDL.AiringStatus = MalAnimeDL.AiringStatus.Undefined,
   val episodesType: RangeMap<MalAnimeDL.EpisodesType> = RangeMap(),
   val nextEpIn: Duration = 0L.milliseconds,
   val nextEp: Int = 0,
   val hasNotificationsOn: Boolean = false
) : ListItem {

   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Render(modifier: Modifier) {
      Card(
         modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(modifier)
      ) {
         Row {

            BlurredGlideImage(
               model = mainPictureMedium,
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
                  title,
                  color = MaterialTheme.colorScheme.primary,
                  fontWeight = FontWeight.Bold
               )

               Spacer(Modifier.weight(1F))

               Row(
                  horizontalArrangement = Arrangement.End,
                  modifier = Modifier
                     .padding(10.dp)
                     .fillMaxWidth()
               ) {
                  Text(
                     "$myListStatusNumEpisodesWatched / $numEpisodes",
                     color = MaterialTheme.colorScheme.primary
                  )
               }
               Box(
                  Modifier.padding()
               ) {
                  LinearProgressIndicator(
                     progress = {
                        if (nextEp > 0 && numEpisodes == 0)
                           1F
                        else if (nextEp > 0 && numEpisodes > 0 && nextEp <= numEpisodes)
                           nextEp / numEpisodes.toFloat()
                        else 0F
                     },
                     drawStopIndicator = { },
                     modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                     color = MaterialTheme.colorScheme.tertiary,
                     strokeCap = StrokeCap.Round,
                     gapSize = 0.dp
                  )

                  LinearProgressIndicator(
                     progress = {
                        if (numEpisodes > 0)
                           myListStatusNumEpisodesWatched / numEpisodes.toFloat()
                        else 0F
                     },
                     drawStopIndicator = { },
                     modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp),
                     color = MaterialTheme.colorScheme.primary,
                     trackColor = Color.Transparent,
                     strokeCap = StrokeCap.Round,
                     gapSize = 0.dp
                  )
               }
            }
         }
      }
   }
}


@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
fun MalAnimeListItemPreview() {

   val anime = MalAnimeListItem(
      1,
      "Title",
      "",
      12,
      5,
      MyListStatus.Status.Watching,
      MalAnimeDL.AiringStatus.FinishedAiring,
      RangeMap(mutableMapOf(1..3 to MalAnimeDL.EpisodesType.MangaCanon)),
      nextEpIn = 5.days,
      8
   )
   AnyMeTheme {
      anime.Render()
   }
}