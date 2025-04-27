package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.anyme.api.MalApi
import com.example.anyme.domain.ui.MalRankingListItem
import com.example.anyme.paging.RankingListPagination
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository
import com.example.anyme.repositories.MalRepository.RankingListType.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
open class RankingListViewModel @Inject constructor(
   private val malRepository: IMalRepository
): ViewModel() {

   @OptIn(ExperimentalPagingApi::class)
   val rankingLists = listOf<Flow<PagingData<MalRankingListItem>>>(
      Pager<Int, MalRankingListItem>(
         PagingConfig(MalApi.RANKING_LIST_PAGE_SIZE, initialLoadSize = MalApi.RANKING_LIST_PAGE_SIZE, prefetchDistance = 1),
         0,
         null
      ) { RankingListPagination(malRepository, All) }.flow.cachedIn(viewModelScope),

//      Pager<Int, MalRankingListItem>(
//         PagingConfig(MalApi.RANKING_LIST_PAGE_SIZE, initialLoadSize = MalApi.RANKING_LIST_PAGE_SIZE),
//         0,
//         null
//      ) { RankingListPagination(malRepository, Airing) }.flow.cachedIn(viewModelScope),

   )


//   @OptIn(ExperimentalPagingApi::class)
//   val all = Pager<Int, MalRankingListItem>(
//      PagingConfig(MalApi.RANKING_LIST_PAGE_SIZE, initialLoadSize = MalApi.RANKING_LIST_PAGE_SIZE, prefetchDistance = 1),
//      0,
//      null
//   ) { RankingListPagination(malRepository, All) }.flow.cachedIn(viewModelScope)




}