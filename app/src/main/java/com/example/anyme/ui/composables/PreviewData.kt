package com.example.anyme.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.anyme.R
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.utils.LocalDateTypeAdapter
import com.example.anyme.utils.OffsetDateTime
import com.example.anyme.utils.OffsetDateTimeAdapter
import com.example.anyme.utils.OffsetWeekTime
import com.example.anyme.utils.OffsetWeekTimeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

@Composable
fun getMediaPreview(): MalAnime {
   val json = stringResource(R.string.placeholder_media).replace("–", "-")
   val gson = GsonBuilder()
      .registerTypeAdapter(LocalDate::class.java, LocalDateTypeAdapter())
      .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
      .registerTypeAdapter(OffsetWeekTime::class.java, OffsetWeekTimeAdapter(TimeZone.of("Asia/Tokyo")))
      .create()

   return gson.fromJson(
      json,
      object : TypeToken<MalAnime>() {}
   )
}