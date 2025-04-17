package com.example.anyme.repositories

import android.util.Log
import com.example.anyme.api.MalApi
import com.example.anyme.daos.MalDao
import com.example.anyme.db.MalDatabase
import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.mal_dl.MyListStatus
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

class MalRepository @Inject constructor(
   private val malApi: MalApi,
   private val malDao: MalDao,
   private val scraper: IEpisodeInfoScraper,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO

) : IMalRepository {

   /**
    * Query the Api to retrieve the user anime list
    */
   override suspend fun retrieveUserAnimeList() {
      withContext(dispatcher) {
         try {
            val epTypeJobs = mutableListOf<Deferred<MalAnimeDL?>>()
            val nextEpJobs = mutableListOf<Deferred<MalAnimeDL?>>()

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

                  // If the anime is not in the watching list, skip
                  if (apiMalAnime.myListStatus.status == MyListStatus.Status.Watching) {
                     val epTypeJob = async(start = CoroutineStart.LAZY) {
                        try {
                           scraper.scrapeEpisodesType(apiMalAnime)
                        } catch(ex: Exception){
                           Log.e(ex.toString(), ex.message ?: "")
                           null
                        }
                     }
                     epTypeJobs.add(epTypeJob)
                  }
                  // If the anime is not airing, skip
                  if (apiMalAnime.status == MalAnimeDL.AiringStatus.CurrentlyAiring ||
                     apiMalAnime.status == MalAnimeDL.AiringStatus.NotYetAired
                  ) {
                     val nextEpJob = async(start = CoroutineStart.LAZY) {
                        try {
                           scraper.scrapeNextEpInfos(apiMalAnime)
                        } catch (ex: Exception) {
                           Log.e(ex.toString(), ex.message ?: "")
                            null
                        }
                     }
                     nextEpJobs.add(nextEpJob)
                  }

                  val dbMalAnime = dbMalAnimeList[entry.key]

                  if (dbMalAnime == null) {
                     malDao.insert(apiMalAnime.mapToMalAnimeDB())
                     return@forEach
                  }

                  // Preserve local data
                  apiMalAnime.copyLocalData(dbMalAnime)
                  if (apiMalAnime == dbMalAnime) {
                     // Remove from the lists all animes which appears in apiMalAnime,
                     // left ones must be deleted from the database
                     dbMalAnimeList.remove(entry.key, dbMalAnime)
                     return@forEach
                  }

                  malDao.update(apiMalAnime.mapToMalAnimeDB())
                  dbMalAnimeList.remove(entry.key, dbMalAnime)
               }
            } while(offset >= 0)

            // Deleting animes from database which are not in the api anime list
            launch { malDao.delete(dbMalAnimeList.values.map { it.mapToMalAnimeDB() }) }

            launch {
               epTypeJobs.forEach {
                  try {
                     it.await()?.let { res ->
                        launch { malDao.update(res.mapToMalAnimeDB()) }
                        delay(500.milliseconds) // To avoid to activate website DDoS protection
                     }
                  } catch (e: Exception) {
                     Log.e(e.toString(), e.message ?: "")
                  }
               }
            }

            launch {
               nextEpJobs.forEach {
                  try {
                     it.await()?.let { res ->
                        launch { malDao.update(res.mapToMalAnimeDB()) }
                        delay(500.milliseconds) // To avoid to activate website DDoS protection
                     }
                  } catch (e: Exception) {
                     Log.e(e.toString(), e.message ?: "")
                  }
               }
            }

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


}