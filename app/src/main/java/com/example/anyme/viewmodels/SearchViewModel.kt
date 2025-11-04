package com.example.anyme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.domain.dl.mal.mapToMalListGridItem
import com.example.anyme.remote.api.MalApi
import com.example.anyme.repositories.paging.SearchingListPagination
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.SettingsRepository
import com.example.anyme.ui.renders.mal.MalAnimeSearchFrameRender
import com.example.anyme.utils.Resource
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

@HiltViewModel
class SearchViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: IMalRepository
) : ViewModel() {


   private val _searchQuery = MutableStateFlow("")

   fun setSearchQuery(searchQuery: String){
      _searchQuery.value = searchQuery
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val searchList = combine(_searchQuery) {
      it.first()
   }.distinctUntilChanged().flatMapLatest {
      malRepository.search(it)
   }.cachedIn(viewModelScope).map { pagingData ->
      val transform = pagingData.map { data ->
         MalAnimeSearchFrameRender(data.mapToMalListGridItem())
      }
      Resource.success(transform)
   }.onStart {
      emit(Resource.loading())
   }.catch { e ->
      if(e is Exception) emit(Resource.failure(e)) else throw e
   }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = Resource.loading()
   )




}