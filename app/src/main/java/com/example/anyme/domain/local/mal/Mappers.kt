package com.example.anyme.domain.local.mal

import android.util.Log
import com.example.anyme.domain.dl.mal.AlternativeTitles
import com.example.anyme.domain.dl.mal.Genre
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MalAnime.EpisodesType
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.domain.dl.mal.Picture
import com.example.anyme.domain.dl.mal.Recommendation
import com.example.anyme.domain.dl.mal.RelatedAnime
import com.example.anyme.domain.dl.mal.Season
import com.example.anyme.domain.dl.mal.Statistics
import com.example.anyme.domain.dl.mal.Status
import com.example.anyme.domain.dl.mal.Studio
import com.example.anyme.utils.OffsetDateTime
import com.example.anyme.utils.OffsetWeekTime
import com.example.anyme.utils.RangeMap
import com.example.anyme.utils.parseOrNull
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

fun MalAnimeDB.mapToMalAnimeDL(gson: Gson): MalAnime {

   try{
      return MalAnime(
         AlternativeTitles(
            alternativeTitlesEn,
            alternativeTitlesJa,
            gson.fromJson(alternativeTitlesSynonyms, object : TypeToken<List<String>>() {})
         ),
         averageEpisodeDuration,
         background,
         OffsetWeekTime.create(
            broadcastDayOfTheWeek.uppercase(),
            broadcastStartTime,
            TimeZone.of("Asia/Tokyo")
         ),
         OffsetDateTime.parse(createdAt),
         LocalDate.parseOrNull(endDate),
         gson.fromJson(genres, object : TypeToken<List<Genre>>() {}),
         id,
         MainPicture(mainPictureLarge, mainPictureMedium),
         mean,
         mediaType,
         MyList(
            myListStatusIsRewatching,
            myListStatusNumEpisodesWatched,
            myListStatusScore,
            MyList.Status.getEnum(myListStatusStatus),
            OffsetDateTime.parse(myListStatusUpdatedAt)
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
         LocalDate.parseOrNull(startDate),
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
         MalAnime.AiringStatus.getEnum(status),
         gson.fromJson(studios, object : TypeToken<List<Studio>>() {}),
         synopsis,
         title,
         OffsetDateTime.parse(updatedAt),
         gson.fromJson(
            episodesType,
            object : TypeToken<RangeMap<EpisodesType>>() {}),
         gson.fromJson(nextEp, NextEpisode::class.java),
         hasNotificationsOn
      )
   } catch (ex: JsonSyntaxException){
      Log.e(ex.toString(), ex.message ?: "")
      return MalAnime()
   }
}