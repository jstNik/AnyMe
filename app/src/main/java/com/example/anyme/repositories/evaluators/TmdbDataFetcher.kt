package com.example.anyme.repositories.evaluators

import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.remote.api.TheMovieDBApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Response
import javax.inject.Inject

class TmdbDataFetcher @Inject constructor(
   private val tmbdApi: TheMovieDBApi,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) {

   suspend fun fetchBannerImage(anime: MalAnime) = coroutineScope {
      var tvSearchDeferred: Deferred<Response<String>>? = null
      var movieSearchDeferred: Deferred<Response<String>>? = null

      if(anime.mediaType == MalAnime.MediaType.Tv || anime.mediaType != MalAnime.MediaType.Movie) {
         tvSearchDeferred = async(dispatcher) {
            tmbdApi.tvSearch(
               anime.title,
               anime.startDate?.year
            )
         }
      }
      if(anime.mediaType == MalAnime.MediaType.Movie || anime.mediaType != MalAnime.MediaType.Tv) {
         movieSearchDeferred = async(dispatcher) {
            tmbdApi.movieSearch(
               anime.title,
               anime.startDate?.toString()
            )
         }
      }



   }

}