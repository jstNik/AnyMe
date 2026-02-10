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
import com.example.anyme.domain.dl.mal.mapToMalAnimeDB
import com.example.anyme.domain.local.mal.mapToMalAnimeDL
import com.example.anyme.data.paging.ExploreListPagination
import com.example.anyme.data.paging.SearchingListPagination
import com.example.anyme.data.visitors.repositories.MalAnimeRepositoryAcceptor
import com.example.anyme.domain.dl.TypeRanking
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.local.db.OrderDirection
import com.example.anyme.utils.getSeason
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.time.toLocalDataTime
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import com.example.type.MediaType
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.getAndUpdate
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

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

   @OptIn(ExperimentalCoroutinesApi::class)
   private fun apiRequestUserList() = flow {
      var offset = 0
      while (offset >= 0) {
         val apiResponse = malApi.retrieveUserAnimeList(offset = offset)
         offset = nextOffset(apiResponse.body()!!.paging)
         emit(apiResponse.body()!!.data)
      }
   }.flowOn(dispatcher).flatMapMerge(concurrency = 5) {
      it.asFlow()
   }.map {
      it.media
   }.catch {

   }

   @OptIn(ExperimentalCoroutinesApi::class)
   private fun scrape(flow: Flow<MalAnime>) = flow.flatMapMerge(5) { anime ->
         channelFlow {
            if(anime.myList.status == MyList.Status.Watching) {
               launch(dispatcher) {
                  runCatching {
                     scraper.scrapeEpisodesType(anime)
                  }.onSuccess {
                     anime.episodesType = it
                     send(anime)
                  }
               }
            }
            if (anime.status == MalAnime.AiringStatus.NotYetAired ||
               anime.status == MalAnime.AiringStatus.CurrentlyAiring
            ) {
               launch(dispatcher) {
                  runCatching {
                     scraper.scrapeNextEpInfos(anime)
                  }.onSuccess {
                     anime.nextEp = it
                     send(anime)
                  }
               }
            }
         }
      }.flowOn(dispatcher)


   /**
    * Query the Api to retrieve the user anime list
    */
   @OptIn(ExperimentalCoroutinesApi::class)
   override suspend fun downloadUserMediaList() {
      withContext(dispatcher) {
         try {
            val mutex = Mutex()
            val dbAnimeList = malDao.fetchAnimeIds().map { it.mapToMalAnimeDL(gson) }.toMutableList()
            val upsertFlow = apiRequestUserList().map { apiAnime ->
               mutex.withLock {
                  val dbAnime = dbAnimeList.firstOrNull { apiAnime.id == it.id }
                  if (dbAnime == null) {
                     apiAnime
                  } else {
                     dbAnimeList.remove(dbAnime)
                     MalSourcesMerger.merge(apiAnime, dbAnime, MalApi.USER_LIST_FIELDS)
                  }
               }
            }

            malDao.upsert(upsertFlow.toList().map { it.mapToMalAnimeDB(gson) })
            if(dbAnimeList.isNotEmpty()) {
               malDao.delete(dbAnimeList.map { it.mapToMalAnimeDB(gson) })
            }

            scrape(upsertFlow).collect {
               malDao.update(it.mapToMalAnimeDB(gson))
            }

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

      animeMap.forEach { (key, value) ->
         val nextEp = scraper.scrapeSeasonal(value)
         animeMap[key]?.let {
            animeMap[key] = it.copy(
               nextEp = nextEp
            )
         }
      }
      Log.d("Seasonal", "Emitting second time")
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


