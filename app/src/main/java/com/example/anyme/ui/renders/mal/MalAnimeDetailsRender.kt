package com.example.anyme.ui.renders.mal

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.dl.mal.AlternativeTitles
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MalAnime.AiringStatus
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.composables.details.GeneralInfoCard
import com.example.anyme.ui.composables.details.NumberPicker
import com.example.anyme.ui.composables.details.RelatedMediaCard
import com.example.anyme.ui.composables.details.TitleCard
import com.example.anyme.ui.composables.details.TitleSection
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.theme.Debug
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.CS
import com.example.anyme.ui.theme.Details
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.ui.theme.TitleStyle
import kotlinx.datetime.TimeZone

class MalAnimeDetailsRender(
   override val media: MalAnimeDetails = MalAnimeDetails(),
   val relatedAnimeRenders: List<MalRelatedItemRender> = emptyList(),
   val recommendationsRenders: List<MalRelatedItemRender> = emptyList()
): MediaDetailsRender {

   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Compose() {

      val navigator = LocalNavHostController.current
      val cs = MaterialTheme.colorScheme

      val scrollState = rememberScrollState()
      val contentPadding = 8.dp
      val externalPadding = 16.dp

      with(media) {

         var modifiedMedia by rememberSaveable {
            mutableStateOf(copy())
         }

         Column(
            horizontalAlignment = Alignment.End,
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
               backgroundPicture = banner,
               alternativeTitle = alternativeTitles.en,
               colors = CardDefaults.cardColors(containerColor = cs.surfaceContainer),
               contentPadding = PaddingValues(contentPadding),
               modifier = Modifier.padding(horizontal = externalPadding),
               debug = Debug
            ) {

               Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                     OutlinedButton(
                        onClick = { },
                        border = BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                     ) {
                        val text = if (myList.status != MyList.Status.Unknown)
                           myList.status.toText() else "Add to list"

                        Text(
                           text = text,
                           style = TitleStyle
                        )

                     }

                     Spacer(modifier = Modifier.weight(1F))

                     val epList = (0..media.numEpisodes).toList()
                     NumberPicker(
                        "${media.myList.numEpisodesWatched}",
                        epList.map { "$it" },
                        TitleStyle
                     ) {
                        if (epList[it] != media.myList.numEpisodesWatched) {
                           modifiedMedia = modifiedMedia.copy(
                              myList = modifiedMedia.myList.copy(
                                 numEpisodesWatched = epList[it]
                              )
                           )
                        }
                     }

                     Text(
                        text = "/",
                        style = TitleStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                     )
                     Text(text = "${media.numEpisodes}", style = TitleStyle)

                     Spacer(modifier = Modifier.weight(1F))

                     val scoreValues = (0..10).toList()
                     NumberPicker(
                        "${media.myList.score}",
                        scoreValues.map { "$it" },
                        TitleStyle
                     ) {
                        if (scoreValues[it] != media.myList.score) {
                           modifiedMedia = modifiedMedia.copy(
                              myList = modifiedMedia.myList.copy(
                                 score = scoreValues[it]
                              )
                           )
                        }
                     }
                     Text(
                        text = "/",
                        style = TitleStyle,
                        modifier = Modifier.padding(horizontal = 4.dp)
                     )
                     Text(text = "10", style = TitleStyle)
                  }

                  if(media != modifiedMedia){
                     Row(
                        modifier = Modifier.fillMaxWidth()
                     ) {
                        OutlinedButton(
                           onClick = {

                           },
                           border = BorderStroke(2.dp, CS.primary),
                           modifier = Modifier.fillMaxWidth()
                        ) {
                           Text(
                              text = "Save edits",
                              style = TitleStyle
                           )
                        }
                     }
                  }
               }
            }

            Spacer(Modifier.height(externalPadding))

            val generalInfos = buildMap {
               if (alternativeTitles != AlternativeTitles())
                  this["Alternative Titles"] = buildMap {
                     if (alternativeTitles.ja.isNotBlank())
                        put("Japanese", alternativeTitles.ja)
                     if (alternativeTitles.synonyms.isNotEmpty())
                        put("Synonyms", alternativeTitles.synonyms.joinToString())
                  }
               this["General Info"] = buildMap {
                  if (mediaType != MalAnime.MediaType.Unknown)
                     put("Media Type", mediaType.toString())
                  if (numEpisodes != 0)
                     put("Episodes", "$numEpisodes")
                  if (status != AiringStatus.Unknown)
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
               if (relatedAnimeRenders.isNotEmpty())
                  put("Related Anime", relatedAnimeRenders)
               if (recommendationsRenders.isNotEmpty())
                  put("Recommended Anime", recommendationsRenders)
            }.entries
            relatedAnime.forEach { (key, value) ->

               RelatedMediaCard(
                  cardTitle = key,
                  medias = value,
                  colors = CardDefaults.cardColors(containerColor = cs.surfaceContainer),
                  contentPadding = PaddingValues(contentPadding),
                  modifier = Modifier.padding(horizontal = externalPadding)
               ) { _, it ->
                  it.Compose {
                     navigator.navigate("$Details/${it.media.host}/${it.media.id}")
                  }
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
fun PreviewMalAnimeDetailsRender() {

   val media = getMediaPreview().mapToMalAnimeDetails()

   AnyMeTheme {
      MalAnimeDetailsRender(
         media,
         media.relatedAnime.map { MalRelatedItemRender(it) },
         media.recommendations.map { MalRelatedItemRender(it) },
      ).Compose()
   }

}