package com.example.anyme.repositories

import android.util.Log
import com.example.anyme.remote.scrapers.HtmlScraper
import com.example.anyme.remote.api.MalApi
import com.example.anyme.local.daos.MalDao
import com.example.anyme.local.db.MalDatabase
import com.example.anyme.domain.remote.mal.Paging
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.domain.dl.mal.mapToMalAnimeDB
import com.example.anyme.domain.dl.mal.mapToMalListGridItem
import com.example.anyme.domain.dl.mal.mapToMalSeasonalListItem
import com.example.anyme.domain.local.mal.mapToMalAnimeDL
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.remote.mal.mapToMalRankingListItem
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.utils.OffsetDateTime
import com.example.anyme.utils.getSeason
import com.example.anyme.utils.toCurrentDateTime
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MalRepository @Inject constructor(
   val malApi: MalApi,
   private val malDao: MalDao,
   private val scraper: HtmlScraper,
   private val gson: Gson,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO

) : IMalRepository {

   enum class RankingListType {
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
   override suspend fun retrieveUserAnimeList() {
      withContext(dispatcher) {
         try {
            var dbMalAnimeList = mutableMapOf<Int, MalAnime>()
            var offset = 0

            do {

               // Call api to retrieve the anime list and compare it wit what inside the database
               val deferredApiResponse = async { malApi.retrieveUserAnimeList(offset = offset) }
               val deferredDbMalAnimeList = async { malDao.fetchAnimeIds() }

               // Check if the api response is successful
               val apiResponse = deferredApiResponse.await()
               validate(apiResponse)

               offset = nextOffset(apiResponse.body()!!.paging)

               // We got the 2 lists, now we have to update both list with the new data
               val apiMalAnimeList = apiResponse.body()!!.`data`.associateBy { it.malAnime.id }
               dbMalAnimeList += deferredDbMalAnimeList.await().associate {
                  it.id to it.mapToMalAnimeDL(gson)
               }.toMutableMap()

               apiMalAnimeList.forEach forEach@{ entry ->
                  val apiMalAnime = entry.value.malAnime

                  val dbMalAnime = dbMalAnimeList[entry.key]

                  val upsertJob = launch {
                     if (dbMalAnime == null) {
                        malDao.insert(apiMalAnime.mapToMalAnimeDB(gson))
                     } else {

                        // Preserve local data
                        apiMalAnime.merge(dbMalAnime)
                        if (apiMalAnime == dbMalAnime) {
                           // Remove from the lists all animes which appears in apiMalAnime,
                           // left ones must be deleted from the database
                           dbMalAnimeList.remove(entry.key, dbMalAnime)
                        } else {

                           malDao.update(apiMalAnime.mapToMalAnimeDB(gson))
                           dbMalAnimeList.remove(entry.key, dbMalAnime)
                        }
                     }
                  }

                  // If the anime is not in the watching list, skip
                  if (apiMalAnime.myList.status == MyList.Status.Watching) {
                     launch {
                        try {
                           val result = scraper.scrapeEpisodesType(apiMalAnime)
                           upsertJob.join()
                           malDao.update(result.mapToMalAnimeDB(gson))
                        } catch (ex: Exception) {
                           Log.e(ex.toString(), ex.message ?: "")
                        }
                     }
                  }
                  // If the anime is not airing, skip
                  if (apiMalAnime.status == MalAnime.AiringStatus.CurrentlyAiring ||
                     apiMalAnime.status == MalAnime.AiringStatus.NotYetAired
                  ) {
                     launch {
                        try {
                           val result = scraper.scrapeNextEpInfos(apiMalAnime)
                           upsertJob.join()
                           malDao.update(result.mapToMalAnimeDB(gson))
                        } catch (ex: Exception) {
                           Log.e(ex.toString(), ex.message ?: "")
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
            } while(offset >= 0)


         } catch (ex: Exception){
            Log.e(ex.toString(), ex.message ?: "")
         }
      }
   }

   override fun fetchMalUserAnime(
      myListStatus: MyList.Status,
      orderBy: MalDatabase.OrderBy,
      orderDirection: MalDatabase.OrderDirection,
      filter: String
   ) = if(orderDirection == MalDatabase.OrderDirection.Asc)
      malDao.fetchUserAnimeAsc(myListStatus.toString(), orderBy.toString(), filter)
   else malDao.fetchUserAnimeDesc(myListStatus.toString(), orderBy.toString(), filter)

   override suspend fun fetchRankingLists(type: RankingListType, offset: Int): List<Data> =
      withContext(dispatcher){
      val response = malApi.retrieveRankingList(type.apiValue, offset = offset)
      validate(response)
      response.body()!!.data
   }

   override suspend fun retrieveMalSeasonalAnimes() = flow {
      val dateTime = Calendar.getInstance().timeInMillis.milliseconds.toCurrentDateTime()
      val animeMap = mutableMapOf<Int, MalAnime>()
      var offset = 0

      do{
         val httpResponse = malApi.retrieveSeasonalAnimes(dateTime.year, dateTime.date.getSeason(), offset)
         validate(httpResponse)
         val dataResponse = httpResponse.body()!!
         animeMap += dataResponse.data.associateBy({ it.malAnime.id },
            { it.malAnime }
         )
         offset = nextOffset(dataResponse.paging)

      } while(offset > 0)

      emit(animeMap.values.toList())

      scraper.scrapeSeasonal(animeMap).forEach { key, value ->
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

   override suspend fun search(title: String, offset: Int): List<MalAnime> {
      val response = if (title.isBlank())
         malApi.retrieveSuggestions(offset)
      else malApi.search(title, offset)

      validate(response)
      val result = response.body()!!

      return result.data.map{
         it.malAnime
      }

   }

   @Throws(IllegalArgumentException::class)
   override suspend fun fetchAnimeDetails(animeId: Int): Flow<MalAnime> {

      val flowAnime = malDao.getAnimeById(animeId).transform { emit(it.mapToMalAnimeDL(gson)) }

      withContext(dispatcher) {
         launch {
            try {
               val apiResponse = malApi.retrieveAnimeDetails(animeId)
               validate(apiResponse)
               apiResponse.body()?.let { apiAnime ->
                  val dbAnime = flowAnime.first()
                  val updateAnime = apiAnime.merge(dbAnime)
                  malDao.update(updateAnime.mapToMalAnimeDB(gson))
               }
            } catch (e: Exception) {
               Log.e("$e", "${e.message}", e)
            }
         }
      }
      return flowAnime
   }


}