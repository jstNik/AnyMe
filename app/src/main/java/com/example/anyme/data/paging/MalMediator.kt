package com.example.anyme.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.domain.local.mal.MalAnimeDB
import com.example.anyme.data.repositories.Repository

@OptIn(ExperimentalPagingApi::class)
class MalMediator(
    private val repo: MalRepository
): RemoteMediator<Int, MalAnimeDB>() {

   override suspend fun load(
      loadType: LoadType,
      state: PagingState<Int, MalAnimeDB>
   ): MediatorResult {

//      repo.retrieveUserAnimeList()
      return MediatorResult.Success(true)

   }
}
