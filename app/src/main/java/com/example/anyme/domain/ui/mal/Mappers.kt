package com.example.anyme.domain.ui.mal

import com.example.anyme.domain.dl.mal.AlternativeTitles
import com.example.anyme.domain.dl.mal.Genre
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MalAnime.AiringStatus
import com.example.anyme.domain.dl.mal.MalAnimeNode
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.domain.dl.mal.Recommendation
import com.example.anyme.domain.dl.mal.RelatedAnime
import com.example.anyme.domain.dl.mal.Season
import com.example.anyme.domain.dl.mal.Statistics
import com.example.anyme.domain.dl.mal.Studio
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.remote.mal.Ranking
import com.example.anyme.remote.Host
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.utils.time.Date
import com.example.anyme.utils.time.OffsetWeekTime
import kotlinx.datetime.number
import kotlin.Int

fun MalAnimeDetails.mapToMalAnime() = MalAnime(
   id = id,
   title = title,
   mainPicture = mainPicture,
   banner = banner,
   mean = mean,
   rank = rank,
   myList = myList,
   alternativeTitles = alternativeTitles,
   averageEpisodeDuration = averageEpisodeDuration,
   broadcast = broadcast,
   endDate = endDate,
   genres= genres,
   mediaType=mediaType,
   nsfw=nsfw,
   numEpisodes=numEpisodes,
   popularity=popularity,
   rating=rating,
   recommendations=recommendations.map{ it.mapToRecommendations() },
   relatedAnime=relatedAnime.map{ it.mapToRelatedAnime() },
   source=source,
   startDate=startDate,
   season=season,
   statistics=statistics,
   status=status,
   studios=studios,
   host=host,
)

fun MalAnimeDetails.MalRelatedAnime.mapToRecommendations() =
   Recommendation(
      MalAnimeNode(
         id, mainPicture, title,
      )
   )

fun MalAnimeDetails.MalRelatedAnime.mapToRelatedAnime() =
   RelatedAnime(
      MalAnimeNode(id, mainPicture, title),
      relationTypeFormatted = relationTypeFormatted ?: ""
   )

fun MalListGridItem.mapToMalAnime() =
   MalAnime(
      id = id,
      title = title,
      mainPicture = mainPicture,
      mean = mean,
      host = host
   )

fun MalRankingListItem.mapToMalAnime() =
   Data(
      MalAnime(
         id = id,
         title = title,
         mainPicture = mainPicture,
         numListUsers = numListUsers,
         host = host
      ),
      ranking = Ranking(rank)
   )

fun MalSeasonalListItem.mapToMalAnime() =
   MalAnime(
      id = id,
      title = title,
      mainPicture = mainPicture,
      startDate = startDate?.dateTime?.let { Date(it.year, it.month.number, it.dayOfMonth) },
      endDate = endDate?.dateTime?.let { Date(it.year, it.month.number, it.dayOfMonth) },
      nextEp = NextEpisode(htmlNextEp, htmlReleaseDate),
      host = host
   )

fun MalUserListItem.mapToMalAnime() =
   MalAnime(
      id = id,
      title = title,
      mainPicture = mainPicture,
      numEpisodes = numEpisodes,
      myList = MyList(numEpisodesWatched = myListStatusNumEpisodesWatched, status = myListStatus),
      status = status,
      episodesType = episodesType,
      nextEp = nextEp,
      hasNotificationsOn = hasNotificationsOn,
      host = host
   )
