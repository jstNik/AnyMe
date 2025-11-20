package com.example.anyme.domain.dl.mal


import com.example.anyme.data.repositories.Repository
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.ConverterVisitor
import com.example.anyme.data.visitors.RepositoryAcceptor
import com.example.anyme.data.visitors.RepositoryVisitor
import com.example.anyme.data.mappers.LayerMapper
import com.example.anyme.data.mappers.MalAnimeLayerMapper
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.RepositoryBundle
import com.example.anyme.data.visitors.MalAnimeRepositoryAcceptor
import com.example.anyme.domain.dl.Media
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.remote.Host
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.time.OffsetWeekTime
import com.example.anyme.utils.RangeMap
import com.example.anyme.utils.time.Date
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.coroutineScope
import java.lang.IllegalArgumentException

data class MalAnime(
   @SerializedName("alternative_titles")
   var alternativeTitles: AlternativeTitles = AlternativeTitles(),
   @SerializedName("average_episode_duration")
   var averageEpisodeDuration: Int = 0,
   @SerializedName("background")
   var background: String = "",
   @SerializedName("broadcast")
   var broadcast: OffsetWeekTime? = null,
   @SerializedName("created_at")
   var createdAt: OffsetDateTime? = null,
   @SerializedName("end_date")
   var endDate: Date? = null,
   @SerializedName("genres")
   var genres: List<Genre> = listOf(),
   @SerializedName("id")
   override var id: Int = 0,
   @SerializedName("main_picture")
   override var mainPicture: MainPicture = MainPicture(),
   @SerializedName("mean")
   var mean: Double = 0.0,
   @SerializedName("media_type")
   var mediaType: MediaType = MediaType.Unknown,
   @SerializedName("my_list_status")
   var myList: MyList = MyList(),
   @SerializedName("nsfw")
   var nsfw: String = "",
   @SerializedName("num_episodes")
   var numEpisodes: Int = 0,
   @SerializedName("num_list_users")
   var numListUsers: Int = 0,
   @SerializedName("num_scoring_users")
   var numScoringUsers: Int = 0,
   @SerializedName("pictures")
   var pictures: List<Picture> = listOf(),
   @SerializedName("popularity")
   var popularity: Int = 0,
   @SerializedName("rank")
   var rank: Int = 0,
   @SerializedName("rating")
   var rating: String = "",
   @SerializedName("recommendations")
   var recommendations: List<Recommendation> = listOf(),
   @SerializedName("related_anime")
   var relatedAnime: List<RelatedAnime> = listOf(),
//    @SerializedName("related_manga")
//    var relatedManga: List<Any?> = listOf(),
   @SerializedName("source")
   var source: String = "",
   @SerializedName("start_date")
   var startDate: Date? =  null,
   @SerializedName("start_season")
   var season: Season = Season(),
   @SerializedName("statistics")
   var statistics: Statistics = Statistics(),
   @SerializedName("status")
   var status: AiringStatus = AiringStatus.Unknown,
   @SerializedName("studios")
   var studios: List<Studio> = listOf(),
   @SerializedName("synopsis")
   var synopsis: String = "",
   @SerializedName("title")
   override var title: String = "",
   @SerializedName("updated_at")
   var updatedAt: OffsetDateTime? = null,

   // Only local values
   var episodesType: RangeMap<EpisodesType> = RangeMap(),
   var nextEp: NextEpisode = NextEpisode(),
   var hasNotificationsOn: Boolean = false,
   override val host: Host = Host.Mal,
   var banner: String = ""

   ) : MalAnimeRepositoryAcceptor, Media {

   enum class EpisodesType {
      MangaCanon {
         override fun toString() = "manga_canon"
      },
      AnimeCanon {
         override fun toString() = "anime_canon"
      },
      MixedMangaCanon {
         override fun toString() = "mixed_manga_canon"
      },
      Filler {
         override fun toString() = "filler"
      },
      Unknown {
         override fun toString() = ""
      };

      companion object {
         fun getEnum(value: String?) = when (value) {
            MangaCanon.toString() -> MangaCanon
            AnimeCanon.toString() -> AnimeCanon
            MixedMangaCanon.toString() -> MixedMangaCanon
            Filler.toString() -> Filler
            else -> Unknown
         }
      }
   }

   enum class AiringStatus {
      NotYetAired {
         override fun toString() = "not_yet_aired"
         override fun toText() = "Not yet aired"
      },
      CurrentlyAiring {
         override fun toString() = "currently_airing"
         override fun toText() = "Currently airing"
      },
      FinishedAiring {
         override fun toString() = "finished_airing"
         override fun toText() = "Finished airing"
      },
      Unknown {
         override fun toString() = ""
         override fun toText() = toString()
      };

      abstract fun toText(): String

      companion object {
         fun getEnum(value: String?) = when (value) {
            NotYetAired.toString() -> NotYetAired
            CurrentlyAiring.toString() -> CurrentlyAiring
            FinishedAiring.toString() -> FinishedAiring
            else -> Unknown
         }
      }

   }

   enum class MediaType {
      Unknown{
         override fun toString(): String = "unknown"
      },
      TV{
         override fun toString(): String = "tv"
      },
      OVA{
         override fun toString(): String = "ova"
      },
      Movie {
         override fun toString(): String = "movie"
      },
      Special {
         override fun toString(): String = "special"
      },
      ONA{
         override fun toString(): String = "ona"
      },
      Music{
         override fun toString(): String = "music"
      };

      companion object {
         fun getEnum(string: String?) = when(string){
            TV.toString() -> TV
            OVA.toString() -> OVA
            Movie.toString() -> Movie
            Special.toString() -> Special
            ONA.toString() -> ONA
            Music.toString() -> Music
            else -> Unknown
         }
      }


   }

   fun merge(dbAnime: MalAnime): MalAnime {

      if(this.id == 0) return dbAnime
      if(dbAnime.id == 0) return this
      if(dbAnime.id != this.id)
         throw IllegalArgumentException("Can't merge two different animes together")

      this.apply {
         episodesType = dbAnime.episodesType
         nextEp = dbAnime.nextEp
         hasNotificationsOn = dbAnime.hasNotificationsOn
//         host = dbAnime.host
         banner = dbAnime.banner
      }

      if(dbAnime.myList.updatedAt == null) return this

      return if (myList.updatedAt == null || dbAnime.myList.updatedAt!! > myList.updatedAt!!)
         this.apply { myList = dbAnime.myList }
      else this
   }

   override suspend  fun <S> acceptRepository(
      repositoryVisitor: RepositoryVisitor,
      bundle: suspend (RepositoryBundle<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption>) -> S
   ): S = repositoryVisitor.visit(this@MalAnime, bundle)


   override fun <T> acceptConverter(
      converterVisitor: ConverterVisitor,
      map: (LayerMapper) -> T
   ): T = converterVisitor.visit(this, map)

}