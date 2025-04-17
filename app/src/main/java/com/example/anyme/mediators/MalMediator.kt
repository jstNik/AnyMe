package com.example.anyme.mediators

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.anyme.domain.mal_db.MalAnimeDB
import com.example.anyme.repositories.IMalRepository

@OptIn(ExperimentalPagingApi::class)
class MalMediator(
    private val repo: IMalRepository
): RemoteMediator<Int, MalAnimeDB>() {

   override suspend fun load(
      loadType: LoadType,
      state: PagingState<Int, MalAnimeDB>
   ): MediatorResult {

      repo.retrieveUserAnimeList()
      return MediatorResult.Success(true)

   }
}
