package com.example.anyme.repositories

import android.util.Log
import androidx.paging.PagingSource
import com.example.anyme.db.MalDatabase
import com.example.anyme.domain.mal_db.MalAnimeDB
import retrofit2.Response

interface IMalRepository {

   suspend fun retrieveUserAnimeList()

   fun fetchMalUserAnime(
      orderBy: MalDatabase.OrderBy,
      orderDirection: MalDatabase.OrderDirection,
      filter: String = ""
   ): PagingSource<Int, MalAnimeDB>

   fun <T> validateResponse(response: Response<T>) {
      if (!response.isSuccessful) {
         val unsuccessful =
            ApiCallNotSuccessfulException("Server replied with ${response.code()} code")
         Log.e("ApiCallNotSuccessfulException", unsuccessful.message ?: "")
         throw unsuccessful
      }
      if (response.body() == null) {
         val unsuccessful = ApiCallNotSuccessfulException("Server replied with null body")
         Log.e("ApiCallNotSuccessfulException", unsuccessful.message ?: "")
         throw unsuccessful
      }
   }
}