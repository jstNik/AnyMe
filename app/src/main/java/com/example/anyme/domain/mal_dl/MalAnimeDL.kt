package com.example.anyme.domain.mal_dl


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anyme.domain.mal_db.MalAnimeDB
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.serialization.mal.AiringStatusDeserializer
import com.example.anyme.serialization.mal.AiringStatusSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

@Entity
data class MalAnimeDL(
    @JsonProperty("alternative_titles")
    var alternativeTitles: AlternativeTitles = AlternativeTitles(),
    @JsonProperty("average_episode_duration")
    var averageEpisodeDuration: Int = 0,
    @JsonProperty("background")
    var background: String = "",
    @JsonProperty("broadcast")
    var broadcast: Broadcast = Broadcast(),
    @JsonProperty("created_at")
    var createdAt: String = "",
    @JsonProperty("end_date")
    var endDate: String = "",
    @JsonProperty("genres")
    var genres: List<Genre> = listOf(),
    @JsonProperty("id")
    override var id: Int = 0,
    @JsonProperty("main_picture")
    var mainPicture: MainPicture = MainPicture(),
    @JsonProperty("mean")
    var mean: Double = 0.0,
    @JsonProperty("media_type")
    var mediaType: String = "",
    @JsonProperty("my_list_status")
    var myListStatus: MyListStatus = MyListStatus(),
    @JsonProperty("nsfw")
    var nsfw: String = "",
    @JsonProperty("num_episodes")
    var numEpisodes: Int = 0,
    @JsonProperty("num_list_users")
    var numListUsers: Int = 0,
    @JsonProperty("num_scoring_users")
    var numScoringUsers: Int = 0,
    @JsonProperty("pictures")
    var pictures: List<Picture> = listOf(),
    @JsonProperty("popularity")
    var popularity: Int = 0,
    @JsonProperty("rank")
    var rank: Int = 0,
    @JsonProperty("rating")
    var rating: String = "",
    @JsonProperty("recommendations")
    var recommendations: List<Recommendation> = listOf(),
    @JsonProperty("related_anime")
    var relatedAnime: List<RelatedAnime> = listOf(),
//    @JsonProperty("related_manga")
//    var relatedManga: List<Any?> = listOf(),
    @JsonProperty("source")
    var source: String = "",
    @JsonProperty("start_date")
    var startDate: String = "",
    @JsonProperty("start_season")
    var startSeason: StartSeason = StartSeason(),
    @JsonProperty("statistics")
    var statistics: Statistics = Statistics(),
    @JsonProperty("status")
    @JsonSerialize(using = AiringStatusSerializer::class)
    @JsonDeserialize(using = AiringStatusDeserializer::class)
    var status: AiringStatus = AiringStatus.Undefined,
    @JsonProperty("studios")
    var studios: List<Studio> = listOf(),
    @JsonProperty("synopsis")
    var synopsis: String = "",
    @JsonProperty("title")
    override var title: String = "",
    @JsonProperty("updated_at")
    var updatedAt: String = "",

    // Only local values
    var episodesType: Map<IntRange, EpisodeType> = mapOf(),
    var nextEpIn: Long = 0L,
    var nextEp: Int = 0,
    var hasNotificationsOn: Boolean = false
): MalAnime {

    enum class EpisodeType{
        MangaCanon{
            override fun toString() = "manga_canon"
        },
        AnimeCanon{
            override fun toString() = "anime_canon"
        },
        MixedMangaCanon{
            override fun toString() = "mixed_manga_canon"
        },
        Filler{
            override fun toString() = "filler"
        },
        Undefined{
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

    enum class AiringStatus{
        NotYetAired{
            override fun toString() = "not_yet_aired"
        },
        CurrentlyAiring{
            override fun toString() = "currently_airing"
        },
        FinishedAiring{
            override fun toString() = "finished_airing"
        },
        Undefined{
            override fun toString() = ""
        };

        companion object{
            fun getEnum(value: String) = when(value){
                NotYetAired.toString() -> NotYetAired
                CurrentlyAiring.toString() -> CurrentlyAiring
                FinishedAiring.toString() -> FinishedAiring
                else -> Undefined
            }
        }

    }

    fun mapToMalAnimeDB() = MalAnimeDB(
        alternativeTitles.en,
        alternativeTitles.ja,
        alternativeTitles.synonyms,
        averageEpisodeDuration,
        background,
        broadcast.dayOfTheWeek,
        broadcast.startTime,
        createdAt,
        endDate,
        genres,
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
        pictures,
        popularity,
        rank,
        rating,
        recommendations,
        relatedAnime,
        listOf(),
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
        studios,
        synopsis,
        title,
        updatedAt,
        episodesType,
        nextEpIn,
        nextEp,
        hasNotificationsOn
    )

}