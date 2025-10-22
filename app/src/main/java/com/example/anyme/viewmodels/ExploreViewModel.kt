package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.domain.remote.mal.mapToMalRankingListItem
import com.example.anyme.remote.api.MalApi
import com.example.anyme.repositories.paging.ExploreListPagination
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository
import com.example.anyme.ui.renders.mal.MalRankingFrameRender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@HiltViewModel
open class ExploreViewModel @Inject constructor(
   private val malRepository: IMalRepository
): ViewModel() {

   private val pagingConfig = PagingConfig(
      MalApi.RANKING_LIST_LIMIT,
      initialLoadSize = MalApi.RANKING_LIST_LIMIT,
      prefetchDistance = 15
   )


   @OptIn(ExperimentalPagingApi::class)
   val rankingLists = MalRepository.RankingListType.entries.map {
      Pager(
         pagingConfig,
         0,
         null
      ){
         ExploreListPagination(malRepository, it)
      }.flow.map { pagingData ->
         pagingData.map { data ->
            MalRankingFrameRender(data.mapToMalRankingListItem())
         }
      }.cachedIn(viewModelScope)
   }

}