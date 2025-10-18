package com.example.anyme.domain.dl.mal

import com.example.anyme.domain.local.mal.MalAnimeDB
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.ui.renders.mal.MalRelatedItemRender
import com.example.anyme.utils.OffsetDateTime
import com.google.gson.Gson
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

fun MalAnime.mapToMalAnimeDB(gson: Gson): MalAnimeDB = MalAnimeDB(
   alternativeTitles.en,
   alternativeTitles.ja,
   gson.toJson(alternativeTitles.synonyms),
   averageEpisodeDuration,
   background,
   "${broadcast?.weekDay ?: ""}",
   broadcast?.time?.toString() ?: "",
   "$createdAt",
   "$endDate",
   gson.toJson(genres),
   id,
   mainPicture.large,
   mainPicture.medium,
   mean,
   mediaType,
   myList.isRewatching,
   myList.numEpisodesWatched,
   myList.score,
   myList.status.toString(),
   "${myList.updatedAt}",
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
   "$startDate",
   season.season,
   season.year,
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
   "$updatedAt",
   gson.toJson(episodesType),
   gson.toJson(nextEp),
   hasNotificationsOn
)

fun MalAnime.mapToMalAnimeListItem(): MalUserListItem =
   MalUserListItem(
      id,
      title,
      mainPicture,
      numEpisodes,
      myList.numEpisodesWatched,
      myList.status,
      status,
      episodesType,
      nextEp,
      hasNotificationsOn
   )

@OptIn(ExperimentalTime::class)
fun MalAnime.mapToMalSeasonalListItem(): MalSeasonalListItem {

   val time = broadcast?.time
   var startDateTime: OffsetDateTime? = null
   var endDateTime: OffsetDateTime? = null
   time?.let{ time ->
      startDate?.let { startDate ->
         startDateTime = OffsetDateTime.create(
            LocalDateTime(startDate, time),
            TimeZone.of("Asia/Tokyo")
         )
      }
      endDate?.let { endDate ->
         endDateTime = OffsetDateTime.create(
            LocalDateTime(endDate, time),
            TimeZone.of("Asia/Tokyo")
         )
      }
   }

   return MalSeasonalListItem(
      id,
      title,
      mainPicture,
      startDateTime,
      endDateTime,
      nextEp.number,
      nextEp.releaseDate
   )
}

fun MalAnime.mapToMalListGridItem(): MalListGridItem = MalListGridItem(
   id,
   title,
   mainPicture,
   mean
)

fun MalAnime.mapToMalAnimeDetails(): MalAnimeDetails = MalAnimeDetails(
   id = id,
   title = title,
   mainPicture = mainPicture,
   mean = mean,
   rank = rank,
   myList = myList,
   alternativeTitles = alternativeTitles,
   averageEpisodeDuration = averageEpisodeDuration,
   broadcast = broadcast,
   endDate = endDate,
   genres = genres,
   mediaType = mediaType,
   nsfw = nsfw,
   numEpisodes = numEpisodes,
   popularity = popularity,
   rating = rating,
   recommendations = recommendations.map { MalRelatedItemRender(it.mapToMalRelatedAnime()) },
   relatedAnime = relatedAnime.map { MalRelatedItemRender(it.mapToMalRelatedAnime()) },
   source = source,
   startDate = startDate,
   season = season,
   statistics = statistics,
   status = status,
   studios = studios
)

fun MalAnimeNode.mapToMalAnimeDL() = MalAnime(
   id = id, mainPicture = mainPicture, title = title
)

fun RelatedAnime.mapToMalRelatedAnime() = MalAnimeDetails.MalRelatedAnime(
   media.id,
   media.title,
   media.mainPicture,
   relationTypeFormatted
)

fun Recommendation.mapToMalRelatedAnime() = MalAnimeDetails.MalRelatedAnime(
   media.id, media.title, media.mainPicture
)