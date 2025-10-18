package com.example.anyme.repositories

import android.util.Log
import androidx.paging.PagingSource
import com.example.anyme.local.db.MalDatabase
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.local.mal.MalAnimeDB
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.repositories.MalRepository.RankingListType
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface IMalRepository {

   suspend fun retrieveUserAnimeList()

   fun fetchMalUserAnime(
      myListStatus: MyList.Status,
      orderBy: MalDatabase.OrderBy,
      orderDirection: MalDatabase.OrderDirection,
      filter: String = ""
   ): PagingSource<Int, MalAnimeDB>

   suspend fun retrieveMalSeasonalAnimes(): Flow<List<MalAnime>>

   suspend fun fetchRankingLists(type: RankingListType, offset: Int): List<Data>

   suspend fun search(title: String, offset: Int): List<MalAnime>

   suspend fun fetchAnimeDetails(animeId: Int): Flow<MalAnime>

   fun <T> validate(response: Response<T>) {
      if (!response.isSuccessful || response.body() == null) {
         val unsuccessful =
            ApiCallNotSuccessfulException(response.raw())
         Log.e("$unsuccessful", unsuccessful.message, unsuccessful)
         throw unsuccessful
      }
   }
}