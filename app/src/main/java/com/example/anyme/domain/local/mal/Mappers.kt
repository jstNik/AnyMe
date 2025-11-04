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
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.time.OffsetWeekTime
import com.example.anyme.utils.RangeMap
import com.example.anyme.utils.time.Date
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

fun MalAnimeDB.mapToMalAnimeDL(gson: Gson): MalAnime {

   try{
      return MalAnime(
         alternativeTitles = AlternativeTitles(
            alternativeTitlesEn,
            alternativeTitlesJa,
            gson.fromJson(alternativeTitlesSynonyms, object : TypeToken<List<String>>() {})
         ),
         averageEpisodeDuration = averageEpisodeDuration,
         background = background,
         broadcast = OffsetWeekTime.create(
            broadcastDayOfTheWeek.uppercase(),
            broadcastStartTime
         ),
         createdAt = OffsetDateTime.parse(createdAt),
         endDate = Date.parse(endDate),
         genres = gson.fromJson(genres, object : TypeToken<List<Genre>>() {}),
         id = id,
         mainPicture = MainPicture(mainPictureLarge, mainPictureMedium),
         mean = mean,
         mediaType = try {
            MalAnime.MediaType.valueOf(mediaType)
         } catch (e: Exception) {
            Log.e("$e", "${e.message}", e)
            MalAnime.MediaType.Unknown
         },
         myList = MyList(
            myListStatusIsRewatching,
            myListStatusNumEpisodesWatched,
            myListStatusScore,
            MyList.Status.getEnum(myListStatusStatus),
            OffsetDateTime.parse(myListStatusUpdatedAt)
         ),
         nsfw = nsfw,
         numEpisodes = numEpisodes,
         numListUsers = numListUsers,
         numScoringUsers = numScoringUsers,
         pictures = gson.fromJson(pictures, object : TypeToken<List<Picture>>() {}),
         popularity = popularity,
         rank = rank,
         rating = rating,
         recommendations = gson.fromJson(
            recommendations,
            object : TypeToken<List<Recommendation>>() {}),
         relatedAnime = gson.fromJson(relatedAnime, object : TypeToken<List<RelatedAnime>>() {}),
         source = source,
         startDate = Date.parse(startDate),
         season = Season(startSeasonSeason, startSeasonYear),
         statistics = Statistics(
            statisticsNumListUsers,
            Status(
               statisticsStatusCompleted,
               statisticsStatusDropped,
               statisticsStatusOnHold,
               statisticsStatusPlanToWatch,
               statisticsStatusWatching
            )
         ),
         status = MalAnime.AiringStatus.getEnum(status),
         studios = gson.fromJson(studios, object : TypeToken<List<Studio>>() {}),
         synopsis = synopsis,
         title = title,
         updatedAt = OffsetDateTime.parse(updatedAt),
         episodesType = gson.fromJson(
            episodesType,
            object : TypeToken<RangeMap<EpisodesType>>() {}),
         nextEp = gson.fromJson(nextEp, NextEpisode::class.java),
         hasNotificationsOn = hasNotificationsOn,
         host = host
      )
   } catch (ex: JsonSyntaxException){
      Log.e(ex.toString(), ex.message ?: "")
      return MalAnime()
   }
}