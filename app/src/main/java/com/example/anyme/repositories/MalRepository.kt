package com.example.anyme.repositories

import android.util.Log
import com.example.anyme.api.MalApi
import com.example.anyme.daos.MalDao
import com.example.anyme.db.MalDatabase
import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.mal_dl.MyListStatus
import com.example.anyme.domain.ui.MalRankingListItem
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MalRepository @Inject constructor(
   val malApi: MalApi,
   private val malDao: MalDao,
   private val scraper: EpisodeInfoScraper,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO

) : IMalRepository {

   enum class RankingListType {
      Tv, Movie, All, Airing,
      Popularity{
         override val apiValue: String = "bypopularity"
      },
      Favorite, Upcoming, Ova, Special;

      open val apiValue: String = toString().lowercase()

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
               validateResponse(apiResponse)

               val idx = apiResponse.body()!!.paging.next.indexOf("offset=", ignoreCase = true) + "offset=".length
               offset = if(idx >= 0 && idx < apiResponse.body()!!.paging.next.length)
                  apiResponse.body()!!.paging.next.elementAt(idx).digitToInt()
               else -1

               // We got the 2 lists, now we have to update both list with the new data
               val apiMalAnimeList = apiResponse.body()!!.`data`.associateBy { it.malAnimeDL.id }
               dbMalAnimeList += deferredDbMalAnimeList.await().associate {
                  it.id to it.mapToMalAnimeDL()
               }.toMutableMap()

               apiMalAnimeList.forEach forEach@{ entry ->
                  val apiMalAnime = entry.value.malAnimeDL

                  val dbMalAnime = dbMalAnimeList[entry.key]

                  val upsertJob = launch {
                     if (dbMalAnime == null) {
                        malDao.insert(apiMalAnime.mapToMalAnimeDB())
                     } else {

                        // Preserve local data
                        apiMalAnime.copyLocalData(dbMalAnime)
                        if (apiMalAnime == dbMalAnime) {
                           // Remove from the lists all animes which appears in apiMalAnime,
                           // left ones must be deleted from the database
                           dbMalAnimeList.remove(entry.key, dbMalAnime)
                        } else {

                           malDao.update(apiMalAnime.mapToMalAnimeDB())
                           dbMalAnimeList.remove(entry.key, dbMalAnime)
                        }
                     }
                  }

                  // If the anime is not in the watching list, skip
                  if (apiMalAnime.myListStatus.status == MyListStatus.Status.Watching) {
                     launch {
                        try {
                           val result = scraper.scrapeEpisodesType(apiMalAnime)
                           upsertJob.join()
                           malDao.update(result.mapToMalAnimeDB())
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
                           malDao.update(result.mapToMalAnimeDB())
                        } catch (ex: Exception) {
                           Log.e(ex.toString(), ex.message ?: "")
                        }
                     }
                  }

               }
               // Deleting animes from database which are not in the api anime list
               launch {
                  malDao.delete(dbMalAnimeList.values.map {
                     dbMalAnimeList.remove(it.id)
                     it.mapToMalAnimeDB()
                  })
               }
            } while(offset >= 0)


         } catch (ex: Exception){
            Log.e(ex.toString(), ex.message ?: "")
         }
      }
   }

   override fun fetchMalUserAnime(
      orderBy: MalDatabase.OrderBy,
      orderDirection: MalDatabase.OrderDirection,
      filter: String
   ) = malDao.fetchUserAnime(orderBy.toString(), orderDirection.toString())

   override suspend fun fetchRankingLists(type: RankingListType, offset: Int): List<MalRankingListItem> = withContext(dispatcher){
      val response = malApi.retrieveRankingList(type.apiValue, offset = offset)
      validateResponse(response)
      response.body()!!.data.map{
         it.mapToMalRankingListItem()
      }
   }

}