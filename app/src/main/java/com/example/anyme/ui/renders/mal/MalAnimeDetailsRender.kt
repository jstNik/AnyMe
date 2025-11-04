package com.example.anyme.ui.renders.mal

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.dl.mal.AlternativeTitles
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MalAnime.AiringStatus
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.composables.details.GeneralInfoCard
import com.example.anyme.ui.composables.details.RelatedMediaCard
import com.example.anyme.ui.composables.details.TitleCard
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.theme.Debug
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.ui.theme.AnyMeTheme
import kotlinx.datetime.TimeZone

class MalAnimeDetailsRender(
   override val media: MalAnimeDetails = MalAnimeDetails()
): MediaDetailsRender {

   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Compose() {

      val cs = MaterialTheme.colorScheme

      val scrollState = rememberScrollState()
      val contentPadding = 8.dp
      val externalPadding = 16.dp

      with(media) {
         Column(
            modifier = Modifier
               .fillMaxSize()
               .background(cs.background)
               .verticalScroll(scrollState)
         ) {

            TitleCard(
               title = title,
               leftStat = "Score" to mean,
               rightStat = "Rank" to rank,
               mainPicture = mainPicture.large,
               backgroundPicture = "",
               myList = myList.status,
               alternativeTitle = alternativeTitles.en,
               colors = CardDefaults.cardColors(containerColor = cs.surfaceContainer),
               contentPadding = PaddingValues(contentPadding),
               modifier = Modifier.padding(horizontal = externalPadding),
               debug = Debug
            )

            Spacer(Modifier.height(externalPadding))

            val generalInfos = buildMap {
               if (alternativeTitles != AlternativeTitles())
                  this["Alternative Titles"] = buildMap {
                     if (alternativeTitles.ja.isNotBlank())
                        put("Japanese", alternativeTitles.ja)
                     if (alternativeTitles.synonyms.isNotEmpty())
                        put("Synonims", alternativeTitles.synonyms.joinToString())
                  }
               this["General Info"] = buildMap {
                  if (mediaType != MalAnime.MediaType.Unknown)
                     put("Media Type", mediaType.toString())
                  if (numEpisodes != 0)
                     put("Episodes", "$numEpisodes")
                  if (status != AiringStatus.Undefined)
                     put("Status", status.toText())
                  if (startDate != null)
                     put("Aired", "$startDate" + if (endDate != null) " to $endDate" else "")
                  if (broadcast != null)
                     put(
                        "Broadcast time",
                        broadcast.toZone(TimeZone.currentSystemDefault()).toText()
                     )
                  if (studios.isNotEmpty())
                     put("Studios", studios.joinToString { s -> s.name })
                  if (source.isNotBlank())
                     put("Source", source.capitalize(Locale.current).replace("-", " "))
                  if (genres.isNotEmpty())
                     put("Genres", genres.joinToString { g -> g.name })
                  if (averageEpisodeDuration != 0)
                     put("Average episode duration", "$averageEpisodeDuration")
                  if (rating.isNotBlank())
                     put("Rating", rating)
               }
            }

            GeneralInfoCard(
               infos = generalInfos,
               contentPadding = PaddingValues(contentPadding),
               colors = CardDefaults.cardColors(cs.surfaceContainer),
               modifier = Modifier.padding(
                  start = externalPadding,
                  end = externalPadding
               )
            )

            Spacer(Modifier.height(externalPadding))

            val relatedAnime = buildMap {
               if (relatedAnime.isNotEmpty())
                  put("Related Anime", relatedAnime)
               if (recommendations.isNotEmpty())
                  put("Recommended Anime", recommendations)
            }.entries
            relatedAnime.forEachIndexed { idx, entry ->

               RelatedMediaCard(
                  cardTitle = entry.key,
                  medias = entry.value,
                  colors = CardDefaults.cardColors(containerColor = cs.surfaceContainer),
                  contentPadding = PaddingValues(contentPadding),
                  modifier = Modifier.padding(horizontal = externalPadding)
               ) { idx, it ->
                  it.Compose(
//                     colors = CardDefaults.cardColors(containerColor = cs.surfaceContainerHighest),
//                     contentPadding = PaddingValues(contentPadding),
//                     modifier = Modifier
//                        .height(
//                           if (it.relationTypeFormatted != null) 270.dp else 225.dp
//                        )
//                        .padding(end = contentPadding)
                  ) { }
               }

               Spacer(Modifier.height(externalPadding))
            }


         }
      }
   }

}

@Preview(showBackground = false,
   uiMode = Configuration.UI_MODE_NIGHT_YES or Configuration.UI_MODE_TYPE_NORMAL
)
@Composable
fun PreviewMalAnimeDetailsRender(){

   val media = getMediaPreview()

   AnyMeTheme {
      MalAnimeDetailsRender(media.mapToMalAnimeDetails()).Compose()
   }

}