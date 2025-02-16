package com.example.anyme.domain.mal_db


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anyme.domain.mal_dl.AlternativeTitles
import com.example.anyme.domain.mal_dl.Broadcast
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.domain.mal_dl.Genre
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.mal_dl.MyListStatus
import com.example.anyme.domain.mal_dl.Picture
import com.example.anyme.domain.mal_dl.Recommendation
import com.example.anyme.domain.mal_dl.RelatedAnime
import com.example.anyme.domain.mal_dl.StartSeason
import com.example.anyme.domain.mal_dl.Statistics
import com.example.anyme.domain.mal_dl.Status
import com.example.anyme.domain.mal_dl.Studio
import com.example.anyme.serialization.mal.EpisodeTypeDeserializer
import com.example.anyme.serialization.mal.EpisodesTypeSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import java.io.Serializable

@Entity
data class MalAnimeDB(
    @JsonProperty("alternative_titles_en")
    var alternativeTitlesEn: String = "",
    @JsonProperty("alternative_titles_ja")
    var alternativeTitlesJa: String = "",
    @JsonProperty("alternative_titles_synonyms")
    var alternativeTitlesSynonyms: List<String> = listOf(),
    @JsonProperty("average_episode_duration")
    var averageEpisodeDuration: Int = 0,
    @JsonProperty("background")
    var background: String = "",
    @JsonProperty("broadcast_day_of_the_week")
    var broadcastDayOfTheWeek: String = "",
    @JsonProperty("broadcast_start_time")
    var broadcastStartTime: String = "",
    @JsonProperty("created_at")
    var createdAt: String = "",
    @JsonProperty("end_date")
    var endDate: String = "",
    @JsonProperty("genres")
    var genres: List<Genre> = listOf(),
    @JsonProperty("id")
    @PrimaryKey
    override var id: Int = 0,
    @JsonProperty("main_picture_large")
    var mainPictureLarge: String = "",
    @JsonProperty("main_picture_medium")
    var mainPictureMedium: String = "",
    @JsonProperty("mean")
    var mean: Double = 0.0,
    @JsonProperty("media_type")
    var mediaType: String = "",
    @JsonProperty("my_list_status_is_rewatching")
    var myListStatusIsRewatching: Boolean = false,
    @JsonProperty("my_list_status_num_episodes_watched")
    var myListStatusNumEpisodesWatched: Int = 0,
    @JsonProperty("my_list_status_score")
    var myListStatusScore: Int = 0,
    @JsonProperty("my_list_status_status")
    var myListStatusStatus: String = "",
    @JsonProperty("my_list_status_updated_at")
    var myListStatusUpdatedAt: String = "",
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
    @JsonProperty("related_manga")
    var relatedManga: List<String> = listOf(),
    @JsonProperty("source")
    var source: String = "",
    @JsonProperty("start_date")
    var startDate: String = "",
    @JsonProperty("start_season_season")
    var startSeasonSeason: String = "",
    @JsonProperty("start_season_year")
    var startSeasonYear: Int = 0,
    @JsonProperty("statistics_num_list_users")
    var statisticsNumListUsers: Int = 0,
    @JsonProperty("statistics_status_completed")
    var statisticsStatusCompleted: String = "",
    @JsonProperty("statistics_status_dropped")
    var statisticsStatusDropped: String = "",
    @JsonProperty("statistics_status_on_hold")
    var statisticsStatusOnHold: String = "",
    @JsonProperty("statistics_status_plan_to_watch")
    var statisticsStatusPlanToWatch: String = "",
    @JsonProperty("statistics_status_watching")
    var statisticsStatusWatching: String = "",
    @JsonProperty("status")
    var status: String = "",
    @JsonProperty("studios")
    var studios: List<Studio> = listOf(),
    @JsonProperty("synopsis")
    var synopsis: String = "",
    @JsonProperty("title")
    override var title: String = "",
    @JsonProperty("updated_at")
    var updatedAt: String = "",
    // Only local values
    @JsonProperty("episodes_type")
    @JsonSerialize(using = EpisodesTypeSerializer::class)
    @JsonDeserialize(using = EpisodeTypeDeserializer::class)
    var episodesType: Map<IntRange, MalAnimeDL.EpisodeType> = mapOf(),
    @JsonProperty("next_ep_in")
    var nextEpIn: Long = 0L,
    @JsonProperty("next_ep")
    var nextEp: Int = 0,
    @JsonProperty("has_notifications_on")
    var hasNotificationsOn: Boolean = false
): MalAnime, Serializable {

    fun mapToMalAnimeDL() = MalAnimeDL(
        AlternativeTitles(alternativeTitlesEn, alternativeTitlesJa, alternativeTitlesSynonyms),
        averageEpisodeDuration,
        background,
        Broadcast(broadcastDayOfTheWeek, broadcastStartTime),
        createdAt,
        endDate,
        genres, id,
        MainPicture(mainPictureLarge, mainPictureMedium),
        mean,
        mediaType,
        MyListStatus(myListStatusIsRewatching, myListStatusNumEpisodesWatched, myListStatusScore, MyListStatus.Status.getEnum(myListStatusStatus), myListStatusUpdatedAt),
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
        source,
        startDate,
        StartSeason(startSeasonSeason, startSeasonYear),
        Statistics(statisticsNumListUsers, Status(statisticsStatusCompleted, statisticsStatusDropped, statisticsStatusOnHold, statisticsStatusPlanToWatch, statisticsStatusWatching)),
        MalAnimeDL.AiringStatus.getEnum(status),
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