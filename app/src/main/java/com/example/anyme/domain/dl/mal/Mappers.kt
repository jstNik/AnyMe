package com.example.anyme.domain.dl.mal

import com.example.anyme.domain.local.mal.MalAnimeDB
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.remote.Host
import com.example.anyme.ui.renders.mal.MalRelatedItemRender
import com.example.anyme.utils.time.OffsetDateTime
import com.google.gson.Gson
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlin.time.ExperimentalTime

fun MalAnime.mapToMalAnimeDB(gson: Gson): MalAnimeDB = MalAnimeDB(
   alternativeTitlesEn = alternativeTitles.en,
   alternativeTitlesJa = alternativeTitles.ja,
   alternativeTitlesSynonyms = gson.toJson(alternativeTitles.synonyms),
   averageEpisodeDuration = averageEpisodeDuration,
   background = background,
   broadcastDayOfTheWeek = "${broadcast?.weekDay ?: ""}",
   broadcastStartTime = broadcast?.let{ "${it.time}${it.offset}" } ?: "",
   createdAt = "${createdAt ?: ""}",
   endDate = "${endDate ?: ""}",
   genres = gson.toJson(genres),
   id = id,
   mainPictureLarge = mainPicture.large,
   mainPictureMedium = mainPicture.medium,
   mean = mean,
   mediaType = mediaType.toString(),
   myListStatusIsRewatching = myList.isRewatching,
   myListStatusNumEpisodesWatched = myList.numEpisodesWatched,
   myListStatusScore = myList.score,
   myListStatusStatus = myList.status.toString(),
   myListStatusUpdatedAt = "${myList.updatedAt ?: ""}",
   nsfw = nsfw,
   numEpisodes = numEpisodes,
   numListUsers = numListUsers,
   numScoringUsers = numScoringUsers,
   pictures = gson.toJson(pictures),
   popularity = popularity,
   rank = rank,
   rating = rating,
   recommendations = gson.toJson(recommendations),
   relatedAnime = gson.toJson(relatedAnime),
   relatedManga = gson.toJson(listOf<Any>()),
   source = source,
   startDate = "${startDate ?: ""}",
   startSeasonSeason = season.season,
   startSeasonYear = season.year,
   statisticsNumListUsers = statistics.numListUsers,
   statisticsStatusCompleted = statistics.status.completed,
   statisticsStatusDropped = statistics.status.dropped,
   statisticsStatusOnHold = statistics.status.onHold,
   statisticsStatusPlanToWatch = statistics.status.planToWatch,
   statisticsStatusWatching = statistics.status.watching,
   status = status.toString(),
   studios = gson.toJson(studios),
   synopsis = synopsis,
   title = title,
   updatedAt = "${updatedAt ?: ""}",
   episodesType = gson.toJson(episodesType),
   nextEp = gson.toJson(nextEp),
   hasNotificationsOn = hasNotificationsOn,
   host = host
)

fun MalAnime.mapToMalAnimeListItem(): MalUserListItem =
   MalUserListItem(
      id = id,
      title = title,
      mainPicture = mainPicture,
      numEpisodes = numEpisodes,
      myListStatusNumEpisodesWatched = myList.numEpisodesWatched,
      myListStatus = myList.status,
      status = status,
      episodesType = episodesType,
      nextEp = nextEp,
      hasNotificationsOn = hasNotificationsOn,
      host = host
   )

@OptIn(ExperimentalTime::class)
fun MalAnime.mapToMalSeasonalListItem(): MalSeasonalListItem {

   val time = broadcast?.time
   var startDateTime: OffsetDateTime? = null
   var endDateTime: OffsetDateTime? = null
   time?.let{ time ->
      startDate?.toLocalDate()?.let { startDate ->
         startDateTime = OffsetDateTime.create(
            LocalDateTime(startDate, time),
            TimeZone.of("Asia/Tokyo")
         )
      }
      endDate?.toLocalDate()?.let { endDate ->
         endDateTime = OffsetDateTime.create(
            LocalDateTime(endDate, time),
            TimeZone.of("Asia/Tokyo")
         )
      }
   }

   return MalSeasonalListItem(
      id = id,
      title = title,
      mainPicture = mainPicture,
      startDate = startDateTime,
      endDate = endDateTime,
      htmlNextEp = nextEp.number,
      htmlReleaseDate = nextEp.releaseDate,
      host = host
   )
}

fun MalAnime.mapToMalListGridItem(): MalListGridItem = MalListGridItem(
   id = id,
   title = title,
   mainPicture = mainPicture,
   mean = mean,
   host = host
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
   studios = studios,
   host = host
)

fun MalAnimeNode.mapToMalAnimeDL() = MalAnime(
   id = id, mainPicture = mainPicture, title = title, host = Host.Mal
)

fun RelatedAnime.mapToMalRelatedAnime() = MalAnimeDetails.MalRelatedAnime(
   id = media.id,
   title = media.title,
   mainPicture = media.mainPicture,
   relationTypeFormatted = relationTypeFormatted,
   host = Host.Mal
)

fun Recommendation.mapToMalRelatedAnime() = MalAnimeDetails.MalRelatedAnime(
   id = media.id, title = media.title, mainPicture = media.mainPicture, host = Host.Mal
)