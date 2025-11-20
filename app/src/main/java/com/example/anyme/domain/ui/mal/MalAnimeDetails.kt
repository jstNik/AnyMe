package com.example.anyme.domain.ui.mal

import android.os.Parcelable
import androidx.compose.runtime.Immutable
import com.example.anyme.data.mappers.LayerMapper
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.ConverterVisitor
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.AlternativeTitles
import com.example.anyme.domain.dl.mal.Genre
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MalAnime.AiringStatus
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.Season
import com.example.anyme.domain.dl.mal.Statistics
import com.example.anyme.domain.dl.mal.Studio
import com.example.anyme.remote.Host
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.utils.time.Date
import com.example.anyme.utils.time.OffsetWeekTime
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
@Immutable
data class MalAnimeDetails(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val mean: Double = 0.0,
   val rank: Int = 0,
   val myList: MyList = MyList(),
   val alternativeTitles: AlternativeTitles = AlternativeTitles(),
   val averageEpisodeDuration: Int = 0,
   val broadcast: OffsetWeekTime? = null,
   val endDate: Date? = null,
   val genres: List<Genre> = listOf(),
   val mediaType: MalAnime.MediaType = MalAnime.MediaType.Unknown,
   val nsfw: String = "",
   val numEpisodes: Int = 0,
   val popularity: Int = 0,
   val rating: String = "",
   val recommendations: List<MalRelatedAnime> = listOf(),
   val relatedAnime: List<MalRelatedAnime> = listOf(),
   val source: String = "",
   val startDate: Date? = null,
   val season: Season = Season(),
   val statistics: Statistics = Statistics(),
   val status: AiringStatus = AiringStatus.Unknown,
   val studios: List<Studio> = listOf(),
   override val host: Host = Host.Mal,
   val banner: String = ""
) : Media, ConverterAcceptor, Parcelable {

   @Serializable
   @Parcelize
   data class MalRelatedAnime(
      override val id: Int = 0,
      override val title: String = "",
      override val mainPicture: MainPicture = MainPicture(),
      val relationTypeFormatted: String? = null,
      override val host: Host = Host.Mal
   ): Media, ConverterAcceptor, Parcelable {

      override fun <T> acceptConverter(
         converterVisitor: ConverterVisitor,
         map: (LayerMapper) -> T
      ): T = converterVisitor.visit(this, map)

   }

   override fun <T> acceptConverter(
      converterVisitor: ConverterVisitor,
      map: (LayerMapper) -> T
   ): T = converterVisitor.visit(this, map)
}