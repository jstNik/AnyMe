package com.example.anyme.repositories

import android.util.Log
import androidx.paging.PagingData
import com.example.anyme.api.HtmlScraper
import com.example.anyme.api.MalApi
import com.example.anyme.daos.MalDao
import com.example.anyme.db.MalDatabase
import com.example.anyme.domain.mal_api.Paging
import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.mal_dl.MyList
import com.example.anyme.domain.ui.MalListGridItem
import com.example.anyme.domain.ui.MalRankingListItem
import com.example.anyme.domain.ui.MalSeasonalListItem
import com.example.anyme.utils.getSeason
import com.example.anyme.utils.toLocalDateTime
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
            var dbMalAnimeList = mutableMapOf<Int, MalAnimeDL>()
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
               val apiMalAnimeList = apiResponse.body()!!.`data`.associateBy { it.malAnimeDL.id }
               dbMalAnimeList += deferredDbMalAnimeList.await().associate {
                  it.id to it.mapToMalAnimeDL(gson)
               }.toMutableMap()

               apiMalAnimeList.forEach forEach@{ entry ->
                  val apiMalAnime = entry.value.malAnimeDL

                  val dbMalAnime = dbMalAnimeList[entry.key]

                  val upsertJob = launch {
                     if (dbMalAnime == null) {
                        malDao.insert(apiMalAnime.mapToMalAnimeDB(gson))
                     } else {

                        // Preserve local data
                        apiMalAnime.copyLocalData(dbMalAnime)
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
                  if (apiMalAnime.status == MalAnimeDL.AiringStatus.CurrentlyAiring ||
                     apiMalAnime.status == MalAnimeDL.AiringStatus.NotYetAired
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


   override suspend fun fetchRankingLists(type: RankingListType, offset: Int): List<MalRankingListItem> = withContext(dispatcher){
      val response = malApi.retrieveRankingList(type.apiValue, offset = offset)
      validate(response)
      response.body()!!.data.map{
         it.mapToMalRankingListItem()
      }
   }

   override suspend fun retrieveMalSeasonalAnimes() = flow {
      val dateTime = Calendar.getInstance().timeInMillis.milliseconds.toLocalDateTime()
      val animeMap = mutableMapOf<Int, MalSeasonalListItem>()
      var offset = 0

      do{
         val httpResponse = malApi.retrieveSeasonalAnimes(dateTime.year, dateTime.date.getSeason(), offset)
         validate(httpResponse)
         val dataResponse = httpResponse.body()!!
         animeMap += dataResponse.data.associateBy({ it.malAnimeDL.id },
            { it.malAnimeDL.mapToMalSeasonalListItem() }
         )
         offset = nextOffset(dataResponse.paging)

      } while(offset > 0)

      emit(animeMap.values.toList())

      scraper.scrapeSeasonal(animeMap).forEach { key, value ->
         animeMap[key]?.let {
            animeMap[key] = it.copy(
               htmlNextEp = value.number, htmlReleaseDate = value.releaseDate.toLocalDateTime()
            )
         }
      }

      emit(animeMap.values.toList())

   }

   override suspend fun search(title: String, offset: Int): List<MalListGridItem> {
      val response = if (title.isBlank())
         malApi.retrieveSuggestions(offset)
      else malApi.search(title, offset)

      validate(response)
      val result = response.body()!!

      return result.data.map{
         it.malAnimeDL.mapToMalListGridItem()
      }

   }

}