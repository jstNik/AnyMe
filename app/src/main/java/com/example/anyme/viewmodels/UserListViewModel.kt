package com.example.anyme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.ExperimentalPagingApi
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.local.db.MalDatabase
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.mapToMalAnimeListItem
import com.example.anyme.domain.local.mal.mapToMalAnimeDL
import com.example.anyme.domain.ui.Settings
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.SettingsRepository
import com.example.anyme.ui.renders.mal.MalUserListAnimeRender
import com.example.anyme.utils.Resource
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class UserListViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: IMalRepository,
   private val gson: Gson
): ViewModel() {

   private data class PagingParams(
      val myListStatus: MyList.Status,
      val orderBy: MalDatabase.OrderBy,
      val orderDirection: MalDatabase.OrderDirection,
      val filter: String
   )

   private val _myListStatus = MutableStateFlow(MyList.Status.Watching)
   private val _orderBy = MutableStateFlow(MalDatabase.OrderBy.LastUpdateAt)
   private val _orderDirection = MutableStateFlow(MalDatabase.OrderDirection.Asc)
   private val _filter = MutableStateFlow("")

   fun setMyListStatus(status: MyList.Status) {
      _myListStatus.value = status
   }
   fun setOrderBy(orderBy: MalDatabase.OrderBy) {
      _orderBy.value = orderBy
   }
   fun setOrderDirection(orderDirection: MalDatabase.OrderDirection) {
      _orderDirection.value = orderDirection
   }
   fun setFilter(filter: String) {
      _filter.value = filter
   }

   private val currentParams = combine(
      _myListStatus,
      _orderBy,
      _orderDirection,
      _filter
   ) { status, orderBy, orderDirection, filter ->
      PagingParams(status, orderBy, orderDirection, filter)
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val list = currentParams.distinctUntilChanged().flatMapLatest { params ->
      malRepository.fetchMalUserAnime(
         params.myListStatus,
         params.orderBy,
         params.orderDirection,
         params.filter
      )
   }.cachedIn(viewModelScope).map{ pagingData ->
      pagingData.map {
         MalUserListAnimeRender(it.mapToMalAnimeListItem())
      }
   }

   val settings = settingsRepo.flow
      .stateIn(
         viewModelScope,
         SharingStarted.Eagerly,
         Settings.DEFAULT
      )

   fun changeSettings(settings: Settings){
      viewModelScope.launch {
         settingsRepo.changeSettings(settings)
      }
   }

}