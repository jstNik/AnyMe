package com.example.anyme.ui.renders.mal

import android.content.res.Configuration
import android.icu.util.Calendar
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.core.graphics.alpha
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.domain.dl.mal.mapToMalAnimeListItem
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.ui.composables.ListEntry
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.Orange
import com.example.anyme.ui.theme.cs
import com.example.anyme.ui.theme.stepBased
import com.example.anyme.ui.theme.typo
import com.example.anyme.utils.RangeMap
import com.example.anyme.utils.time.Offset
import com.example.anyme.utils.time.OffsetDateTime
import kotlinx.coroutines.delay
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MalUserListAnimeRender(
   override val media: MalUserListItem = MalUserListItem()
) : MediaListItemRender {

   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Compose(
      onClick: () -> Unit
   ) {

      val nextEp = media.nextEp.number
      var countdown by remember {
         mutableStateOf("")
      }
      val inverseSurfaceColor = Color(
         red = 255 - cs.surfaceContainer.toArgb().red,
         green = 255 - cs.surfaceContainer.toArgb().green,
         blue = 255 - cs.surfaceContainer.toArgb().green,
         alpha = cs.surfaceContainer.toArgb().alpha
      )


      LaunchedEffect(media.nextEp) {

         val nextEp = if (nextEp > 0) "Ep $nextEp in "
         else "Next ep in "

         val today = System.currentTimeMillis().milliseconds
         val releaseDate = media.nextEp.releaseDate
            ?.toZone(TimeZone.currentSystemDefault())
            ?.dateTime
            ?.toInstant(TimeZone.currentSystemDefault())
            ?.toEpochMilliseconds()
            ?.milliseconds
         if (releaseDate != null && releaseDate > today) {
            var millis = releaseDate - today + 1.minutes
            // FIXME
            while (millis > 0.milliseconds) {
               millis.toComponents { days, hours, minutes, _, _ ->
                  val s = mutableListOf("${days}d", "${hours}h", "${minutes}min")
                  if (days <= 0L)
                     s.remove("${days}d")
                  if (hours <= 0)
                     s.remove("${hours}h")
                  if (minutes <= 0 || days > 0 && hours > 0)
                     s.remove("${minutes}min")
                  countdown = nextEp + s.joinToString(" ")
               }
               val extraSeconds = millis - millis.inWholeMinutes.minutes
               val waitingTime = if (extraSeconds > 0.seconds)
                  millis - millis.inWholeMinutes.minutes else 1.minutes
               delay(waitingTime)
               millis -= waitingTime
            }
            countdown = ""
         }

      }

      with(media) {
         ListEntry(
            media = media,
            imageHeight = 128.dp,
            contentPadding = PaddingValues(8.dp),
            maxLines = 1,
            titleAutoSize = typo.titleMedium.fontSize.stepBased(0.8),
            titleStyle = typo.titleMedium.copy(color = cs.primary),
            colors = CardDefaults.cardColors(
               containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            onClick = onClick
         ) {

            Box(
               modifier = Modifier
                  .fillMaxWidth()
                  .weight(1F)
                  .padding(end = 8.dp, bottom = 8.dp)
            ) {

               if (countdown.isNotBlank())
                  Text(
                     text = countdown,
                     style = typo.bodySmall.copy(color = cs.surfaceContainer),
                     modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(start = 4.dp)
                        .clip(RoundedCornerShape(percent = 100))
                        .background(
                           lerp(
                              cs.onTertiaryFixedVariant,
                              inverseSurfaceColor,
                              0.3F
                           )
                        )
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                  )

               Column(
                  modifier = Modifier.align(Alignment.TopStart)
               ) {
                  if (myListIsRewatching) {
                     Text(
                        text = "Rewatching",
                        style = typo.bodySmall.copy(color = cs.surfaceContainer),
                        modifier = Modifier
                           .padding(bottom = 8.dp)
                           .clip(RoundedCornerShape(percent = 100))
                           .background(
                              lerp(
                                 cs.onTertiaryFixedVariant,
                                 inverseSurfaceColor,
                                 0.3F
                              )
                           )
                           .padding(vertical = 4.dp, horizontal = 8.dp)
                     )
                  }

                  episodesType.getEntry(myListStatusNumEpisodesWatched + 1)?.let {
                     if (it.value != MalAnime.EpisodesType.Unknown)
                        Text(
                           text = if (it.key.first != it.key.last) "Ep ${it.key.first} - ${it.key.last}"
                           else "Ep ${it.key.first}",
                           style = typo.bodySmall.copy(color = cs.surfaceContainer),
                           modifier = Modifier
                              .clip(RoundedCornerShape(percent = 100))
                              .background(
                                 when (it.value) {
                                    MalAnime.EpisodesType.MangaCanon -> lerp(
                                       Color.Green,
                                       inverseSurfaceColor,
                                       0.3F
                                    )

                                    MalAnime.EpisodesType.AnimeCanon -> lerp(
                                       Color.Blue,
                                       inverseSurfaceColor,
                                       0.3F
                                    )

                                    MalAnime.EpisodesType.MixedMangaCanon -> lerp(
                                       Color.Orange,
                                       inverseSurfaceColor,
                                       0.3F
                                    )

                                    MalAnime.EpisodesType.Filler -> lerp(
                                       Color.Red,
                                       inverseSurfaceColor,
                                       0.3F
                                    )

                                    else -> Color.Unspecified
                                 }
                              )
                              .padding(vertical = 4.dp, horizontal = 8.dp)
                        )
                  }
               }

               Text(
                  "$myListStatusNumEpisodesWatched / $numEpisodes",
                  style = typo.bodyLarge,
                  color = MaterialTheme.colorScheme.primary,
                  modifier = Modifier
                     .align(Alignment.BottomEnd)
               )
            }
            Box {

               LinearProgressIndicator(
                  progress = {
                     if (nextEp == 0 && numEpisodes == 0)
                        0F
                     else if (nextEp > numEpisodes)
                        1F
                     else nextEp / numEpisodes.toFloat()
                  },
                  drawStopIndicator = { },
                  modifier = Modifier
                     .fillMaxWidth()
                     .height(4.dp),
                  color = lerp(
                     cs.surfaceContainerHighest,
                     inverseSurfaceColor,
                     0.3F
                  ),
                  trackColor = lerp(
                     cs.surfaceContainerHigh,
                     inverseSurfaceColor,
                     0.1F
                  ),
                  strokeCap = StrokeCap.Round,
                  gapSize = 0.dp
               )


               LinearProgressIndicator(
                  progress = {
                     when (numEpisodes) {
                        0 if myListStatusNumEpisodesWatched == 0 -> 0F
                        0 if myListStatusNumEpisodesWatched > 0 -> 1F
                        else -> myListStatusNumEpisodesWatched / numEpisodes.toFloat()
                     }
                  },
                  drawStopIndicator = { },
                  modifier = Modifier
                     .fillMaxWidth()
                     .height(4.dp),
                  color = MaterialTheme.colorScheme.tertiary,
                  trackColor = Color.Transparent,
                  strokeCap = StrokeCap.Round,
                  gapSize = 0.dp
               )
            }
         }
      }
   }

}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL)
@Composable
fun PreviewMalUserListItem() {

   val media = getMediaPreview()
   media.myList.isRewatching = true
   media.myList.numEpisodesWatched = 10
   media.nextEp = NextEpisode(
      13, OffsetDateTime.create(
         LocalDateTime(2026, 6, 21, 14, 30),
         Offset(1.hours)
      )
   )
   media.episodesType =
      RangeMap(mutableMapOf(11..12 to MalAnime.EpisodesType.AnimeCanon))

   AnyMeTheme {
      MalUserListAnimeRender(media.mapToMalAnimeListItem()).Compose {

      }
   }

}