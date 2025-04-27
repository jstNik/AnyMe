package com.example.anyme.domain.mal_dl


import androidx.room.Entity
import com.example.anyme.domain.mal_db.MalAnimeDB
import com.example.anyme.domain.ui.MalAnimeListItem
import com.example.anyme.domain.ui.MalRankingListItem
import com.example.anyme.utils.RangeMap
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName

@Entity
data class MalAnimeDL(
   @SerializedName("alternative_titles")
   var alternativeTitles: AlternativeTitles = AlternativeTitles(),
   @SerializedName("average_episode_duration")
   var averageEpisodeDuration: Int = 0,
   @SerializedName("background")
   var background: String = "",
   @SerializedName("broadcast")
   var broadcast: Broadcast = Broadcast(),
   @SerializedName("created_at")
   var createdAt: String = "",
   @SerializedName("end_date")
   var endDate: String = "",
   @SerializedName("genres")
   var genres: List<Genre> = listOf(),
   @SerializedName("id")
   var id: Int = 0,
   @SerializedName("main_picture")
   var mainPicture: MainPicture = MainPicture(),
   @SerializedName("mean")
   var mean: Double = 0.0,
   @SerializedName("media_type")
   var mediaType: String = "",
   @SerializedName("my_list_status")
   var myListStatus: MyListStatus = MyListStatus(),
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
   var startDate: String = "",
   @SerializedName("start_season")
   var startSeason: StartSeason = StartSeason(),
   @SerializedName("statistics")
   var statistics: Statistics = Statistics(),
   @SerializedName("status")
   var status: AiringStatus = AiringStatus.Undefined,
   @SerializedName("studios")
   var studios: List<Studio> = listOf(),
   @SerializedName("synopsis")
   var synopsis: String = "",
   @SerializedName("title")
   var title: String = "",
   @SerializedName("updated_at")
   var updatedAt: String = "",

   // Only local values
   var episodesType: RangeMap<EpisodesType> = RangeMap(),
   var nextEp: NextEpisode = NextEpisode(),
   var hasNotificationsOn: Boolean = false
) : MalAnime {

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
      Undefined {
         override fun toString() = ""
      };

      companion object {
         fun getEnum(value: String) = when (value) {
            MangaCanon.toString() -> MangaCanon
            AnimeCanon.toString() -> AnimeCanon
            MixedMangaCanon.toString() -> MixedMangaCanon
            Filler.toString() -> Filler
            else -> Undefined
         }
      }
   }

   enum class AiringStatus {
      NotYetAired {
         override fun toString() = "not_yet_aired"
      },
      CurrentlyAiring {
         override fun toString() = "currently_airing"
      },
      FinishedAiring {
         override fun toString() = "finished_airing"
      },
      Undefined {
         override fun toString() = ""
      };

      companion object {
         fun getEnum(value: String) = when (value) {
            NotYetAired.toString() -> NotYetAired
            CurrentlyAiring.toString() -> CurrentlyAiring
            FinishedAiring.toString() -> FinishedAiring
            else -> Undefined
         }
      }

   }

   fun copyLocalData(malAnime: MalAnimeDL) {
      episodesType = malAnime.episodesType
      nextEp = malAnime.nextEp
      hasNotificationsOn = malAnime.hasNotificationsOn
   }

   fun mapToMalAnimeDB(): MalAnimeDB {

      val gson = Gson()

      return MalAnimeDB(
         alternativeTitles.en,
         alternativeTitles.ja,
         gson.toJson(alternativeTitles.synonyms),
         averageEpisodeDuration,
         background,
         broadcast.dayOfTheWeek,
         broadcast.startTime,
         createdAt,
         endDate,
         gson.toJson(genres),
         id,
         mainPicture.large,
         mainPicture.medium,
         mean,
         mediaType,
         myListStatus.isRewatching,
         myListStatus.numEpisodesWatched,
         myListStatus.score,
         myListStatus.status.toString(),
         myListStatus.updatedAt,
         nsfw,
         numEpisodes,
         numListUsers,
         numScoringUsers,
         gson.toJson(pictures),
         popularity,
         rank,
         rating,
         gson.toJson(recommendations),
         gson.toJson(relatedAnime),
         gson.toJson(listOf<Any>()),
         source,
         startDate,
         startSeason.season,
         startSeason.year,
         statistics.numListUsers,
         statistics.status.completed,
         statistics.status.dropped,
         statistics.status.onHold,
         statistics.status.planToWatch,
         statistics.status.watching,
         status.toString(),
         gson.toJson(studios),
         synopsis,
         title,
         updatedAt,
         gson.toJson(episodesType),
         gson.toJson(nextEp),
         hasNotificationsOn
      )

   }

   fun mapToMalAnimeListItem(): MalAnimeListItem =
      MalAnimeListItem(
         id,
         title,
         mainPicture.medium,
         numEpisodes,
         myListStatus.numEpisodesWatched,
         myListStatus.status,
         status,
         episodesType,
         nextEp.nextEpIn,
         nextEp.nextEp,
         hasNotificationsOn
      )

}