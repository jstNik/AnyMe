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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
open class RankingListViewModel @Inject constructor(
   private val malRepository: IMalRepository
): ViewModel() {

   private val pagingConfig = PagingConfig(
      MalApi.RANKING_LIST_LIMIT,
      initialLoadSize = MalApi.RANKING_LIST_LIMIT,
      prefetchDistance = 15
   )


   @OptIn(ExperimentalPagingApi::class)
   val rankingLists = MalRepository.RankingListType.entries.map {
      Pager<Int, MalRankingListItem>(
         pagingConfig,
         0,
         null
      ){
         RankingListPagination(malRepository, it)
      }.flow.cachedIn(viewModelScope)
   }

}