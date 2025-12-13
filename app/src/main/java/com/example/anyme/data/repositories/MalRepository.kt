package com.example.anyme.data.repositories

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.apollographql.apollo.ApolloClient
import com.example.BackgroundBannerQuery
import com.example.anyme.data.evaluators.MalSourcesMerger
import com.example.anyme.di.AniListClient
import com.example.anyme.remote.scrapers.HtmlScraper
import com.example.anyme.remote.api.MalApi
import com.example.anyme.local.daos.MalDao
import com.example.anyme.domain.remote.mal.Paging
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.domain.dl.mal.mapToMalAnimeDB
import com.example.anyme.domain.local.mal.mapToMalAnimeDL
import com.example.anyme.data.paging.ExploreListPagination
import com.example.anyme.data.paging.SearchingListPagination
import com.example.anyme.data.visitors.repositories.MalAnimeRepositoryAcceptor
import com.example.anyme.domain.dl.TypeRanking
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.local.db.OrderDirection
import com.example.anyme.utils.Resource.Status
import com.example.anyme.utils.getSeason
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.time.toLocalDataTime
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import com.example.type.MediaType
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.getAndUpdate

class MalRepository @Inject constructor(
   private val malApi: MalApi,
   @param:AniListClient private val aniListApi: ApolloClient,
   private val malDao: MalDao,
   private val scraper: HtmlScraper,
   private val gson: Gson,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : Repository<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption> {

   enum class MalRankingTypes : TypeRanking {
      Tv,
      Movie,
      All,
      Airing,
      Popularity {
         override val apiValue: String = "bypopularity"
      },
      Favorite,
      Upcoming,
      Ova,
      Special;

      open val apiValue: String = toString().lowercase()

   }

   private fun nextOffset(paging: Paging): Int {
      val offsetAsString = paging.next.substringAfter("offset=", "").substringBefore("&")
      return try {
         Integer.parseInt(offsetAsString)
      } catch (_: NumberFormatException) {
         -1
      }
   }


   /**
    * Query the Api to retrieve the user anime list
    */
   override suspend fun downloadUserMediaList() {
      withContext(dispatcher) {
         try {
            val dbMalAnimeList = mutableMapOf<Int, MalAnime>()
            var offset = 0

            do {

               // Call api to retrieve the anime list and compare it wit what inside the database
               val deferredApiResponse = async { malApi.retrieveUserAnimeList(offset = offset) }
               val deferredDbMalAnimeList = async { malDao.fetchAnimeIds() }

               // Check if the api response is successful
               val apiResponse = deferredApiResponse.await()

               offset = nextOffset(apiResponse.body()!!.paging)

               // We got the 2 lists, now we have to update both list with the new data
               val apiMalAnimeList = apiResponse.body()!!.`data`.associateBy { it.id }
               dbMalAnimeList += deferredDbMalAnimeList.await().associate {
                  it.id to it.mapToMalAnimeDL(gson)
               }.toMutableMap()

               apiMalAnimeList.forEach forEach@{ (id, data) ->
                  val apiMalAnime = data.media
                  val dbMalAnime = dbMalAnimeList[id]
                  val mergedAnime = if (dbMalAnime == null) apiMalAnime
                  else MalSourcesMerger.merge(apiMalAnime, dbMalAnime, MalApi.USER_LIST_FIELDS)

                  val upsertJob = launch {
                     if (dbMalAnime == null) {
                        malDao.insert(apiMalAnime.mapToMalAnimeDB(gson))
                     } else {
                        // Preserve local data
                        if (mergedAnime.myList != dbMalAnime.myList)
                        // Remove from the lists all animes which appears in apiMalAnime,
                        // left ones must be deleted from the database
                           malDao.update(mergedAnime.copy().mapToMalAnimeDB(gson))
                        dbMalAnimeList.remove(id, dbMalAnime)

                     }
                  }

                  // If the anime is not in the watching list, skip
                  if (mergedAnime.myList.status == MyList.Status.Watching) {
                     launch {
                        try {
                           val result = scraper.scrapeEpisodesType(mergedAnime)
                           upsertJob.join()
                           malDao.update(result.copy().mapToMalAnimeDB(gson))
                        } catch (e: Exception) {
                           Log.e("$e", "${e.message}", e)
                        }
                     }
                  }
                  // If the anime is not airing, skip
                  if (mergedAnime.status == MalAnime.AiringStatus.CurrentlyAiring ||
                     mergedAnime.status == MalAnime.AiringStatus.NotYetAired
                  ) {
                     launch {
                        try {
                           val result = scraper.scrapeNextEpInfos(mergedAnime)
                           upsertJob.join()
                           malDao.update(result.copy().mapToMalAnimeDB(gson))
                        } catch (e: Exception) {
                           Log.e("$e", "${e.message}", e)
                        }
                     }
                  }
               }
               // Deleting animes from database which are not in the api anime list
               val animeToDelete = dbMalAnimeList.values.map {
                  dbMalAnimeList.remove(it.id)
                  it.mapToMalAnimeDB(gson)
               }
               launch {
                  malDao.delete(animeToDelete)
               }
            } while (offset >= 0)


         } catch (ex: Exception) {
            Log.e(ex.toString(), ex.message ?: "")
         }
      }
   }

   @OptIn(ExperimentalPagingApi::class)
   override fun fetchMediaUserList(
      myListStatus: MyList.Status,
      orderOption: MalOrderOption,
      filter: String
   ): Flow<PagingData<MalAnimeRepositoryAcceptor>> {
      return Pager(
         PagingConfig(50, 10),
         0,
         null
      ) {
         if (orderOption.direction == OrderDirection.Asc)
            malDao.fetchUserAnimeAsc(myListStatus.toString(), orderOption.by.toString(), filter)
         else malDao.fetchUserAnimeDesc(myListStatus.toString(), orderOption.by.toString(), filter)
      }.flow.map { data ->
         data.map {
            it.mapToMalAnimeDL(gson)
         }
      }
   }

   @OptIn(ExperimentalPagingApi::class)
   override fun fetchRankingLists(type: MalRankingTypes): Flow<PagingData<MalAnimeRepositoryAcceptor>> {
      return Pager(
         PagingConfig(
            MalApi.RANKING_LIST_LIMIT,
            initialLoadSize = MalApi.RANKING_LIST_LIMIT,
            prefetchDistance = 15
         ),
         0,
         null
      ) {
         ExploreListPagination(malApi, type)
      }.flow
   }

   override fun fetchSeasonalMedia() = flow {
      val dateTime = Calendar.getInstance().timeInMillis.toLocalDataTime()
      val animeMap = mutableMapOf<Int, MalAnime>()
      var offset = 0

      do {
         val httpResponse =
            malApi.retrieveSeasonalAnimes(dateTime.year, dateTime.date.getSeason(), offset)
         val dataResponse = httpResponse.body()!!
         animeMap += dataResponse.data.associateBy(
            { it.media.id },
            { it.media }
         )
         emit(animeMap.values.toList())
         offset = nextOffset(dataResponse.paging)

      } while (offset > 0)

      scraper.scrapeSeasonal(animeMap).forEach { (key, value) ->
         animeMap[key]?.let {
            animeMap[key] = it.copy(
               nextEp = NextEpisode(
                  value.number,
                  value.releaseDate
               )
            )
         }
      }

      emit(animeMap.values.toList())

   }

   @OptIn(ExperimentalPagingApi::class)
   override fun search(searchQuery: String): Flow<PagingData<MalAnimeRepositoryAcceptor>> {
      return Pager(
         PagingConfig(
            MalApi.SEARCHING_LIST_LIMIT,
            1,
            initialLoadSize = MalApi.SEARCHING_LIST_LIMIT
         ),
         0,
         null
      ) {
         SearchingListPagination(malApi, searchQuery)
      }.flow
   }

   private suspend fun updateMediaDetails(media: MalAnime): MalAnime? = withContext(dispatcher) {
      malApi.retrieveAnimeDetails(media.id).body()?.let{
         fetchBanner(it)?.let { banner ->
            it.banner = banner
         }
         it
      }
   }

   @Throws(IllegalArgumentException::class)
   override fun fetchMediaDetails(media: MalAnime, refreshingStatus: RefreshingStatus): Flow<MalAnime> {

      val isFirst = MutableStateFlow(refreshingStatus == RefreshingStatus.InitialRefreshing)
      val isRefreshing = MutableStateFlow(refreshingStatus == RefreshingStatus.Refreshing)

      return malDao.getAnimeById(media.id).map {
         it?.mapToMalAnimeDL(gson)
      }.transform { dbResult ->
         val dbHit = dbResult != null
         if (dbHit && !isRefreshing.value) emit(dbResult)

         if (isFirst.getAndUpdate { false } || isRefreshing.getAndUpdate { false }) {

            updateMediaDetails(media)?.let { apiAnime ->
               if (dbHit) {
                  val syncedAnime =
                     MalSourcesMerger.merge(apiAnime, dbResult, MalApi.DETAILS_FIELDS)
                  withContext(dispatcher) {
                     malDao.update(syncedAnime.mapToMalAnimeDB(gson))
                  }
                  emit(syncedAnime)
               } else {
                  emit(apiAnime)
               }
               return@transform
            }
         }
      }
   }

   private suspend fun fetchBanner(media: MalAnime): String? = withContext(dispatcher) {
      val response = aniListApi
         .query(BackgroundBannerQuery(media.id, MediaType.ANIME))
         .execute()
      val e = response.exception
      if (e != null) {
         Log.e("$e", "${e.message}", e)
         null
      } else {
         response.data?.Media?.bannerImage
      }
   }

   override suspend fun update(media: MalAnime) {
      withContext(dispatcher) {
         val updatedAt = OffsetDateTime.Now
         try {
            malApi.update(
               id = media.id,
               myListStatus = media.myList.status.toString(),
               isRewatching = media.myList.isRewatching,
               score = media.myList.score,
               numEpsWatched = media.myList.numEpisodesWatched,
               priority = media.myList.priority,
               numTimesRewatched = media.myList.numTimesRewatched,
               rewatchValue = media.myList.rewatchValue,
               tags = media.myList.tags?.joinToString(","),
               comments = media.myList.comments
            ).body()!!.let {
               malDao.update(media.copy(myList = it).mapToMalAnimeDB(gson))
            }
         } catch (e: Exception) {
            Log.e("$e", "${e.message}", e)
            malDao.update(
               media.copy(
                  myList = media.myList.copy(
                     updatedAt = updatedAt
                  )
               ).mapToMalAnimeDB(gson)
            )
         }
      }
   }


}


