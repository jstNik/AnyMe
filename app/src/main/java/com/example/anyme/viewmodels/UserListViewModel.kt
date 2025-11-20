package com.example.anyme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.Repository
import com.example.anyme.local.db.MalDatabase
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.ui.Settings
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.ConverterVisitor
import com.example.anyme.domain.dl.ListStatus
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.local.db.OrderOption
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class UserListViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: Repository<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption>,
   private val converterVisitor: ConverterVisitor,
   private val gson: Gson
): ViewModel() {

   private data class PagingParams(
      val myListStatus: MyList.Status,
      val orderOption: MalOrderOption,
      val filter: String
   )

   private val _myListStatus = MutableStateFlow(MyList.Status.Watching)
   private val _orderOption = MutableStateFlow<MalOrderOption>(MalOrderOption.LastUpdatedAt.Asc)
   private val _filter = MutableStateFlow("")

   fun setMyListStatus(status: MyList.Status) {
      _myListStatus.value = status
   }
   fun setOrderOption(orderOption: MalOrderOption) {
      _orderOption.value = orderOption
   }
   fun setFilter(filter: String) {
      _filter.value = filter
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val list = combine(
      _myListStatus,
      _orderOption,
      _filter
   ) { status, orderOption, filter ->
      PagingParams(status, orderOption, filter)
   }.distinctUntilChanged().flatMapLatest { params ->
      malRepository.fetchMediaUserList(
         params.myListStatus,
         params.orderOption,
         params.filter
      )
   }.map{ pagingData ->
      pagingData.map {
         it.acceptConverter(converterVisitor) { media -> media.mapDomainToListItem() }
      }
   }.catch{ e ->
      emit(PagingData.empty(LoadStates(LoadState.Error(e), LoadState.Error(e), LoadState.Error(e))))
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000L),
      PagingData.empty()
   ).cachedIn(viewModelScope)

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