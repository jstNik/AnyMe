package com.example.anyme.domain.local.mal


import androidx.room.Entity
import androidx.room.PrimaryKey
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
    var id: Int = 0,
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
    var title: String = "",
    var updatedAt: String = "",
    // Only local values
    var episodesType: String = "",
    var nextEp: String = "",
    var hasNotificationsOn: Boolean = false
): Serializable