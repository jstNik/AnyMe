package com.example.anyme.ui.composables.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.anyme.R
import com.example.anyme.domain.dl.mal.AlternativeTitles
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MalAnime.AiringStatus
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.utils.DateTypeAdapter
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.OffsetDateTimeAdapter
import com.example.anyme.utils.time.OffsetWeekTime
import com.example.anyme.utils.OffsetWeekTimeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.collections.forEach

@Composable
fun GeneralInfoCard(
   infos: Map<String, Map<String, String>>,
   modifier: Modifier = Modifier,
   contentPadding: PaddingValues = PaddingValues(),
   colors: CardColors = CardDefaults.cardColors()
) {

   val cs = MaterialTheme.colorScheme
   val typo = MaterialTheme.typography

   Card(
      colors = colors,
      modifier = modifier
   ) {

      Column(
         modifier = Modifier.padding(contentPadding)
      ) {

         infos.entries.forEachIndexed { idx, title ->
            TitleSection(
               text = title.key,
               modifier = Modifier
                  .padding(start = 8.dp)
            )

            title.value.forEach {
               Row(
                  horizontalArrangement = Arrangement.SpaceBetween,
                  modifier = Modifier.fillMaxWidth()
               ) {
                  Text(
                     text = it.key,
                     style = typo.bodyMedium,
                     fontWeight = FontWeight.Bold,
                     textAlign = TextAlign.Left,
                     color = cs.secondary,
                     modifier = Modifier.padding(end = 16.dp)
                  )
                  Spacer(Modifier.widthIn(min = 50.dp))
                  Text(
                     text = it.value,
                     style = typo.bodyMedium,
                     textAlign = TextAlign.Center,
                     color = cs.secondary
                  )
               }
            }
            if(idx != infos.entries.size - 1)
               Spacer(modifier = Modifier.padding(bottom = 8.dp))
         }
      }
   }
}

@Preview
@Composable
fun PreviewGeneralInfoCard(){

   val json = stringResource(R.string.placeholder_media).replace("–", "-")
   val gson = GsonBuilder()
      .registerTypeAdapter(LocalDate::class.java, DateTypeAdapter())
      .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
      .registerTypeAdapter(
         OffsetWeekTime::class.java,
         OffsetWeekTimeAdapter(TimeZone.of("Asia/Tokyo"))
      )
      .create()

   val media = gson.fromJson(
      json,
      object : TypeToken<MalAnime>() {}
   )

   val infos = with(media.mapToMalAnimeDetails()) {
      buildMap {
         if (alternativeTitles != AlternativeTitles())
            this["Alternative Titles"] = buildMap {
               /*if (alternativeTitles.en.isNotBlank())
               put("English", alternativeTitles.en)*/
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
               put("Broadcast time", broadcast.toText())
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
   }

   AnyMeTheme {
      GeneralInfoCard(
         infos = infos,
         contentPadding = PaddingValues(8.dp),
         colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
         modifier = Modifier
      )
   }

}