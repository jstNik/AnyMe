package com.example.anyme.repositories

import android.util.Log
import androidx.paging.PagingData
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
   ): Flow<PagingData<MalAnime>>

   fun retrieveMalSeasonalAnimes(): Flow<List<MalAnime>>

   fun fetchRankingLists(type: RankingListType): Flow<PagingData<Data>>

   fun search(searchQuery: String): Flow<PagingData<MalAnime>>

   fun fetchAnimeDetails(animeId: Int): Flow<MalAnime>

   suspend fun synchronizeApiWithDB(dbAnime: MalAnime)

   suspend fun downlaodBanner(anime: MalAnime)
}