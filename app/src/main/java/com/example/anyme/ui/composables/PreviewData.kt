package com.example.anyme.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.anyme.R
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.utils.AiringStatusAdapter
import com.example.anyme.utils.DateTypeAdapter
import com.example.anyme.utils.EpisodesTypeAdapter
import com.example.anyme.utils.MediaTypeAdapter
import com.example.anyme.utils.MyListStatusAdapter
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.OffsetDateTimeAdapter
import com.example.anyme.utils.time.OffsetWeekTime
import com.example.anyme.utils.OffsetWeekTimeAdapter
import com.example.anyme.utils.time.Date
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

@Composable
fun getMediaPreview(): MalAnime {
   val json = stringResource(R.string.placeholder_media).replace("–", "-")
   val gson = GsonBuilder()
      .registerTypeAdapter(Date::class.java, DateTypeAdapter())
      .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
      .registerTypeAdapter(
         OffsetWeekTime::class.java,
         OffsetWeekTimeAdapter(TimeZone.of("Asia/Tokyo"))
      )
      .registerTypeAdapter(MalAnime.EpisodesType::class.java, EpisodesTypeAdapter())
      .registerTypeAdapter(MalAnime.AiringStatus::class.java, AiringStatusAdapter())
      .registerTypeAdapter(MalAnime.MediaType::class.java, MediaTypeAdapter())
      .registerTypeAdapter(MyList.Status::class.java, MyListStatusAdapter())
      .create()

   return gson.fromJson(
      json,
      object : TypeToken<MalAnime>() {}
   )
}