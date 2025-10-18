package com.example.anyme.domain.ui.mal

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
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.AlternativeTitles
import com.example.anyme.domain.dl.mal.Genre
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MalAnime.AiringStatus
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.Season
import com.example.anyme.domain.dl.mal.Statistics
import com.example.anyme.domain.dl.mal.Studio
import com.example.anyme.domain.ui.MediaDetails
import com.example.anyme.domain.ui.MediaListItem
import com.example.anyme.ui.composables.details.GeneralInfoCard
import com.example.anyme.ui.composables.details.RelatedMediaCard
import com.example.anyme.ui.composables.details.TitleCard
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.utils.OffsetWeekTime
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlin.collections.buildMap

@Immutable
data class MalAnimeDetails(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val backgroundPicture: String = "",
   val mean: Double = 0.0,
   val rank: Int = 0,
   val myList: MyList = MyList(),
   val alternativeTitles: AlternativeTitles = AlternativeTitles(),
   val averageEpisodeDuration: Int = 0,
   val broadcast: OffsetWeekTime? = null,
   val endDate: LocalDate? = null,
   val genres: List<Genre> = listOf(),
   val mediaType: String = "",
   val nsfw: String = "",
   val numEpisodes: Int = 0,
   val popularity: Int = 0,
   val rating: String = "",
   val recommendations: List<MediaListItemRender> = listOf(),
   val relatedAnime: List<MediaListItemRender> = listOf(),
   val source: String = "",
   val startDate: LocalDate? = null,
   val season: Season = Season(),
   val statistics: Statistics = Statistics(),
   val status: AiringStatus = AiringStatus.Undefined,
   val studios: List<Studio> = listOf(),
) : Media {
   
   data class MalRelatedAnime(
      override val id: Int = 0,
      override val title: String = "",
      override val mainPicture: MainPicture = MainPicture(),
      val relationTypeFormatted: String? = null
   ): Media

}