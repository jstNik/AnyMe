package com.example.anyme.repositories

import android.util.Log
import androidx.paging.PagingSource
import com.example.anyme.db.MalDatabase
import com.example.anyme.domain.mal_db.MalAnimeDB
import com.example.anyme.domain.ui.MalRankingListItem
import com.example.anyme.domain.ui.MalSeasonalListItem
import com.example.anyme.repositories.MalRepository.RankingListType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IMalRepository {

   suspend fun retrieveUserAnimeList()

   fun fetchMalUserAnime(
      orderBy: MalDatabase.OrderBy,
      orderDirection: MalDatabase.OrderDirection,
      filter: String = ""
   ): PagingSource<Int, MalAnimeDB>

   suspend fun retrieveMalSeasonalAnimes(): Flow<List<MalSeasonalListItem>>

   suspend fun fetchRankingLists(type: RankingListType, offset: Int): List<MalRankingListItem>

   fun <T> validate(response: Response<T>) {
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