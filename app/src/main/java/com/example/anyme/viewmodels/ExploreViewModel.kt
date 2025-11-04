package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.remote.mal.mapToMalRankingListItem
import com.example.anyme.remote.api.MalApi
import com.example.anyme.repositories.paging.ExploreListPagination
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository
import com.example.anyme.repositories.SettingsRepository
import com.example.anyme.ui.renders.mal.MalRankingFrameRender
import com.example.anyme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.associateWith
import kotlin.collections.buildMap

@HiltViewModel
open class ExploreViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: IMalRepository
) : ViewModel() {


   val rankingListFlow = combine(
      MalRepository.RankingListType.entries.map { type ->
         malRepository.fetchRankingLists(type)
            .cachedIn(viewModelScope)
            .map {
               val transformed = it.map { data ->
                  MalRankingFrameRender(data.mapToMalRankingListItem())
               }
               type to Resource.success(transformed)
            }.onStart {
               emit(type to Resource.loading())
            }.catch { e ->
               if (e is Exception) emit(type to Resource.failure(e)) else throw e
            }
      }
   ) {
      it.toList()
   }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = MalRepository.RankingListType.entries.map {
         it to Resource.loading()
      }
   )
}