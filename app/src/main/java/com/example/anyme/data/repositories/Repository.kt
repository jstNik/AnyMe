package com.example.anyme.data.repositories

import androidx.paging.PagingData
import com.example.anyme.data.visitors.repositories.RepositoryAcceptor
import com.example.anyme.domain.dl.ListStatus
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.TypeRanking
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.local.db.OrderOption
import com.example.anyme.utils.Resource
import kotlinx.coroutines.flow.Flow

interface Repository<T: Media, R: TypeRanking, L: ListStatus, O: OrderOption> {

   enum class RefreshingStatus{
      InitialRefreshing, Refreshing, NotRefreshing
   }

   suspend fun downloadUserMediaList()

   fun fetchMediaUserList(
      myListStatus: L,
      orderOption: O,
      filter: String = ""
   ): Flow<PagingData<RepositoryAcceptor<T, R, L, O>>>

   fun fetchSeasonalMedia(): Flow<List<RepositoryAcceptor<T, R, L, O>>>

   fun fetchRankingLists(type: R): Flow<PagingData<RepositoryAcceptor<T, R, L, O>>>

   fun search(searchQuery: String): Flow<PagingData<RepositoryAcceptor<T, R, L, O>>>

   fun fetchMediaDetails(media: T, refreshingStatus: RefreshingStatus): Flow<RepositoryAcceptor<T, R, L, O>>

   suspend fun update(media: T)

}