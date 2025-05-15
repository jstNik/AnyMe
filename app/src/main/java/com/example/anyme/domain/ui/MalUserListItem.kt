package com.example.anyme.domain.ui

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
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.mal_dl.MyListStatus
import com.example.anyme.domain.mal_dl.NextEpisode
import com.example.anyme.ui.composables.BlurredGlideImage
import com.example.anyme.utils.RangeMap

@Immutable
data class MalUserListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val numEpisodes: Int = 0,
   val myListStatusNumEpisodesWatched: Int = 0,
   val myListStatusStatus: MyListStatus.Status = MyListStatus.Status.Undefined,
   val status: MalAnimeDL.AiringStatus = MalAnimeDL.AiringStatus.Undefined,
   val episodesType: RangeMap<MalAnimeDL.EpisodesType> = RangeMap(),
   val nextEp: NextEpisode = NextEpisode(),
   val hasNotificationsOn: Boolean = false
) : MalAnime, ListItem {

   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Render(
      modifier: Modifier,
      onClick: (ListItem) -> Unit
   ) {
      Card(
         onClick = { onClick(this) },
         modifier = Modifier
            .fillMaxWidth()
            .height(128.dp)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .then(modifier)
      ) {
         Row {

            BlurredGlideImage(
               model = mainPicture.medium,
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
                        if (nextEp.number > 0 && numEpisodes == 0)
                           1F
                        else if (nextEp.number > 0 && numEpisodes > 0 && nextEp.number <= numEpisodes)
                           nextEp.number / numEpisodes.toFloat()
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