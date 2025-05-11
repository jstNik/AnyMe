package com.example.anyme.domain.mal_db


import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.anyme.domain.mal_dl.AlternativeTitles
import com.example.anyme.domain.mal_dl.Broadcast
import com.example.anyme.domain.mal_dl.Genre
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.mal_dl.MalAnimeDL.EpisodesType
import com.example.anyme.domain.mal_dl.MyListStatus
import com.example.anyme.domain.mal_dl.NextEpisode
import com.example.anyme.domain.mal_dl.Picture
import com.example.anyme.domain.mal_dl.Recommendation
import com.example.anyme.domain.mal_dl.RelatedAnime
import com.example.anyme.domain.mal_dl.Season
import com.example.anyme.domain.mal_dl.Statistics
import com.example.anyme.domain.mal_dl.Status
import com.example.anyme.domain.mal_dl.Studio
import com.example.anyme.utils.RangeMap
import com.example.anyme.utils.toLocalDate
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import java.io.Serializable

@Entity
data class MalAnimeDB(

    var alternativeTitlesEn: String = "",
    var alternativeTitlesJa: String = "",
    var alternativeTitlesSynonyms: String = "",
    var averageEpisodeDuration: Int = 0,
    var background: String = "",
    var broadcastDayOfTheWeek: String = "",
    var broadcastStartTime: String = "",
    var createdAt: String = "",
    var endDate: String = "",
    var genres: String = "",
    @PrimaryKey
    override var id: Int = 0,
    var mainPictureLarge: String = "",
    var mainPictureMedium: String = "",
    var mean: Double = 0.0,
    var mediaType: String = "",
    var myListStatusIsRewatching: Boolean = false,
    var myListStatusNumEpisodesWatched: Int = 0,
    var myListStatusScore: Int = 0,
    var myListStatusStatus: String = "",
    var myListStatusUpdatedAt: String = "",
    var nsfw: String = "",
    var numEpisodes: Int = 0,
    var numListUsers: Int = 0,
    var numScoringUsers: Int = 0,
    var pictures: String = "",
    var popularity: Int = 0,
    var rank: Int = 0,
    var rating: String = "",
    var recommendations: String = "",
    var relatedAnime: String = "",
    var relatedManga: String = "",
    var source: String = "",
    var startDate: String = "",
    var startSeasonSeason: String = "",
    var startSeasonYear: Int = 0,
    var statisticsNumListUsers: Int = 0,
    var statisticsStatusCompleted: String = "",
    var statisticsStatusDropped: String = "",
    var statisticsStatusOnHold: String = "",
    var statisticsStatusPlanToWatch: String = "",
    var statisticsStatusWatching: String = "",
    var status: String = "",
    var studios: String = "",
    var synopsis: String = "",
    override var title: String = "",
    var updatedAt: String = "",
    // Only local values
    var episodesType: String = "",
    var nextEp: String = "",
    var hasNotificationsOn: Boolean = false
): MalAnime, Serializable {

    fun mapToMalAnimeDL(gson: Gson): MalAnimeDL {

        try{
            return MalAnimeDL(
                AlternativeTitles(
                    alternativeTitlesEn,
                    alternativeTitlesJa,
                    gson.fromJson(alternativeTitlesSynonyms, object : TypeToken<List<String>>() {})
                ),
                averageEpisodeDuration,
                background,
                Broadcast(broadcastDayOfTheWeek, broadcastStartTime),
                createdAt,
                endDate.toLocalDate(),
                gson.fromJson(genres, object : TypeToken<List<Genre>>() {}),
                id,
                MainPicture(mainPictureLarge, mainPictureMedium),
                mean,
                mediaType,
                MyListStatus(
                    myListStatusIsRewatching,
                    myListStatusNumEpisodesWatched,
                    myListStatusScore,
                    MyListStatus.Status.getEnum(myListStatusStatus),
                    myListStatusUpdatedAt
                ),
                nsfw,
                numEpisodes,
                numListUsers,
                numScoringUsers,
                gson.fromJson(pictures, object : TypeToken<List<Picture>>() {}),
                popularity,
                rank,
                rating,
                gson.fromJson(
                    recommendations,
                    object : TypeToken<List<Recommendation>>() {}),
                gson.fromJson(relatedAnime, object : TypeToken<List<RelatedAnime>>() {}),
                source,
                startDate.toLocalDate(),
                Season(startSeasonSeason, startSeasonYear),
                Statistics(
                    statisticsNumListUsers,
                    Status(
                        statisticsStatusCompleted,
                        statisticsStatusDropped,
                        statisticsStatusOnHold,
                        statisticsStatusPlanToWatch,
                        statisticsStatusWatching
                    )
                ),
                MalAnimeDL.AiringStatus.getEnum(status),
                gson.fromJson(studios, object : TypeToken<List<Studio>>() {}),
                synopsis,
                title,
                updatedAt,
                gson.fromJson(
                    episodesType,
                    object : TypeToken<RangeMap<EpisodesType>>() {}),
                gson.fromJson(nextEp, NextEpisode::class.java),
                hasNotificationsOn
            )
        } catch (ex: JsonSyntaxException){
            Log.e(ex.toString(), ex.message ?: "")
            return MalAnimeDL()
        }
    }

}