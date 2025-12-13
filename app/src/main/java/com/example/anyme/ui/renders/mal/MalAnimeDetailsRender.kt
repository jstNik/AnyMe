package com.example.anyme.ui.renders.mal

import android.content.res.Configuration
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.data.visitors.renders.CallbacksBundle
import com.example.anyme.domain.dl.mal.AlternativeTitles
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MalAnime.AiringStatus
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.composables.details.GeneralInfoCard
import com.example.anyme.ui.composables.details.WheelPicker
import com.example.anyme.ui.composables.details.RelatedMediaCard
import com.example.anyme.ui.composables.details.TitleCard
import com.example.anyme.ui.composables.details.WheelPickerBehavior
import com.example.anyme.ui.composables.getMediaPreview
import com.example.anyme.ui.theme.Debug
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.ui.theme.Pages.Companion.DETAILS
import com.example.anyme.ui.theme.TitleStyle
import com.example.anyme.utils.Resource
import kotlinx.datetime.TimeZone

class MalAnimeDetailsRender(
   override val media: MalAnimeDetails = MalAnimeDetails(),
   val relatedAnimeRenders: List<MalRelatedItemRender> = emptyList(),
   val recommendationsRenders: List<MalRelatedItemRender> = emptyList(),
   val callbacks: CallbacksBundle
): MediaDetailsRender {

   @OptIn(ExperimentalGlideComposeApi::class)
   @Composable
   override fun Compose() {
      with(media) {

         val navigator = LocalNavHostController.current
         val updatingStatus = callbacks.updatingStatus
         val cs = MaterialTheme.colorScheme

         val scrollState = rememberScrollState()
         val contentPadding = 8.dp
         val externalPadding = 16.dp
         val alpha by derivedStateOf {
            updatingStatus.status != Resource.Status.Loading
         }
         val pictureWidth = 120.dp
         val pictureHeight = 170.dp
         val yPictureOffset = 90.dp
         val titleStyle = TitleStyle
         val textAutoSize = remember {
            TextAutoSize.StepBased(
               10.sp,
               titleStyle.fontSize
            )
         }

         val density = LocalDensity.current
         var textHeight by remember{
            mutableStateOf(0.dp)
         }

         var edits by rememberSaveable {
             mutableStateOf(myList.copy())
         }

         var userEdited = rememberSaveable(updatingStatus.status) {
            updatingStatus.status != Resource.Status.Success
         }

         LaunchedEffect(myList){
            if(!userEdited)
               edits = myList.copy()
         }



         SwipeUpToRefresh(
            scrollableState = scrollState,
            isRefreshing = callbacks.isRefreshing,
            onRefresh = { callbacks.onRefresh(media) }
         ) {

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
                  pictureWidth = pictureWidth,
                  pictureHeight = pictureHeight,
                  yPictureOffset = yPictureOffset,
                  backgroundPicture = banner,
                  alternativeTitle = alternativeTitles.en,
                  colors = CardDefaults.cardColors(containerColor = cs.surfaceContainer),
                  contentPadding = PaddingValues(contentPadding),
                  modifier = Modifier.padding(horizontal = externalPadding),
                  titleTextAutoSize = textAutoSize,
                  alternativeTitleTextAutoSize = TextAutoSize.StepBased(
                     8.sp, TitleStyle.fontSize * 0.9
                     ),
                  debug = Debug
               ) {

                  Column(
                     modifier = Modifier.animateContentSize()
                  ) {

                     Row(
                        modifier = Modifier.fillMaxWidth()
                     ) {
                        Row(
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.Center,
                           modifier = Modifier.width(pictureWidth)
                        ) {
                           if (myList.status == MyList.Status.Unknown)
                              Button(
                                 onClick = {
                                    edits = edits.copy(
                                       status = MyList.Status.PlanToWatch
                                    )
                                    userEdited = myList != edits
                                 }
                              ) {
                                 Text(
                                    text = "Add to list",
                                    style = TitleStyle.copy(color = cs.onPrimary)
                                 )
                              }
                           else {
                              val listStatus =
                                 MyList.Status.entries.filter { it != MyList.Status.Unknown }

                              WheelPicker(
                                 initialIndex = listStatus.indexOf(edits.status),
                                 textAutoSize = textAutoSize,
                                 behavior = object : WheelPickerBehavior {
                                    override val size = listStatus.size
                                    override fun getText(idx: Int) =
                                       listStatus.getOrNull(idx)?.toText()

                                    override fun getIndexOf(string: String) = -1
                                 },
                                 textStyle = TitleStyle,
                                 wrapUpChoices = true,
                                 enableTextFieldInput = false
                              ) {
                                 edits = edits.copy(
                                    status = listStatus[it]
                                 )
                                 userEdited = myList != edits
                              }
                           }
                        }

                        Spacer(modifier = Modifier.weight(1F))

                        Row(
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.Center
                        ) {
                           WheelPicker(
                              edits.numEpisodesWatched,
                              textAutoSize = textAutoSize,
                              behavior = object : WheelPickerBehavior {
                                 override val size =
                                    if (numEpisodes != 0) numEpisodes + 1 else Int.MAX_VALUE

                                 override fun getText(idx: Int) =
                                    if (idx in 0..<size) "$idx" else null

                                 override fun getIndexOf(string: String) = try {
                                    val value = string.toInt()
                                    if (value in 0..<size) value else -1
                                 } catch (_: NumberFormatException) {
                                    -1
                                 }
                              },
                              textStyle = TitleStyle
                           ) {
                              assert(it in 0..(if (numEpisodes != 0) numEpisodes else Int.MAX_VALUE))
                              edits = edits.copy(
                                 numEpisodesWatched = it
                              )
                              userEdited = myList != edits
                           }

                           Text(
                              text = "/",
                              style = TitleStyle,
                              modifier = Modifier.padding(horizontal = 4.dp)
                           )
                           Text(text = "$numEpisodes", style = TitleStyle)
                        }

                        Spacer(modifier = Modifier.weight(2F))

                        Row(
                           verticalAlignment = Alignment.CenterVertically,
                           horizontalArrangement = Arrangement.Center
                        ) {
                           WheelPicker(
                              initialIndex = edits.score,
                              textAutoSize = textAutoSize,
                              behavior = object : WheelPickerBehavior {
                                 override val size = 11
                                 override fun getText(idx: Int) =
                                    if (idx in 0..<size) "$idx" else null

                                 override fun getIndexOf(string: String) = try {
                                    val value = string.toInt()
                                    if (value in 0..<size) value else -1
                                 } catch (_: NumberFormatException) {
                                    -1
                                 }
                              },
                              textStyle = TitleStyle
                           ) {
                              assert(it in 0..10)
                              edits = edits.copy(
                                 score = it
                              )
                              userEdited = myList != edits
                           }
                           Text(
                              text = "/",
                              style = TitleStyle,
                              modifier = Modifier.padding(horizontal = 4.dp)
                           )
                           Text(text = "10", style = TitleStyle)
                        }
                        Spacer(modifier = Modifier.weight(1F))
                     }

                     if (myList != edits && userEdited) {

                        OutlinedButton(
                           enabled = updatingStatus.status != Resource.Status.Loading,
                           onClick = {
                              callbacks.onSave(media.copy(myList = edits.copy()))
                           },
                           border = BorderStroke(2.dp, cs.primary)
                        ) {
                           Box(
                              contentAlignment = Alignment.Center,
                              modifier = Modifier.fillMaxWidth()
                           ) {
                              Text(
                                 text = if (updatingStatus.status != Resource.Status.Failure)
                                    "Save edits" else "Try again",
                                 style = TitleStyle,
                                 onTextLayout = {
                                    textHeight = with(density) { it.size.height.toDp() }
                                 },
                                 modifier = Modifier.alpha(
                                    if (alpha) 1F
                                    else 0F
                                 )
                              )
                              CircularProgressIndicator(
                                 color = cs.primary,
                                 modifier = Modifier
                                    .size(textHeight)
                                    .alpha(
                                       if (!alpha) 1F
                                       else 0F
                                    )
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
                        navigator.navigate("$DETAILS/${it.media.host}/${it.media.id}")
                     }
                  }
                  Spacer(Modifier.height(externalPadding))
               }
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

   AnyMeTheme {

      val media = getMediaPreview().mapToMalAnimeDetails()

      var render by remember {
         mutableStateOf(
            MalAnimeDetailsRender(
               media,
               media.relatedAnime.map { MalRelatedItemRender(it) },
               media.recommendations.map { MalRelatedItemRender(it) },
               CallbacksBundle(

               )
            )
         )
      }

      render.Compose()
   }

}