package com.example.anyme.data.repositories

import androidx.paging.PagingData
import com.example.anyme.data.visitors.RepositoryAcceptor
import com.example.anyme.domain.dl.ListStatus
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.MediaWrapper
import com.example.anyme.domain.dl.TypeRanking
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.local.db.MalDatabase
import com.example.anyme.local.db.OrderOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface RepositoryBundle<T: Media, R: TypeRanking, L: ListStatus, O: OrderOption> {

   suspend fun downloadUserMediaList()

   fun fetchMediaUserList(
      myListStatus: L,
      orderOption: O,
      filter: String
   ): Flow<PagingData<RepositoryAcceptor<T, R, L, O>>>

   fun fetchSeasonalMedia(): Flow<List<RepositoryAcceptor<T, R, L, O>>>

   fun fetchRankingLists(type: R): Flow<PagingData<RepositoryAcceptor<T, R, L, O>>>

   fun search(searchQuery: String): Flow<PagingData<RepositoryAcceptor<T, R, L, O>>>

   fun fetchMediaDetails(): Flow<RepositoryAcceptor<T, R, L, O>>

}