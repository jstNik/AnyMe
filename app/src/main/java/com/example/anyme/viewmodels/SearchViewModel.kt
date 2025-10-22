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
import com.example.anyme.ui.renders.mal.MalAnimeSearchFrameRender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
   private val malRepository: IMalRepository
) : ViewModel() {

   var searchQuery = ""
      set(value) {
         if (value != field) {
            field = value
            pagingSourceFactory.invalidate()
         }
      }

   private val pagingConfig = PagingConfig(
      MalApi.SEARCHING_LIST_LIMIT,
      1,
      initialLoadSize = MalApi.SEARCHING_LIST_LIMIT
   )

   private val pagingSourceFactory = InvalidatingPagingSourceFactory {
      SearchingListPagination(malRepository, searchQuery)
   }

   @OptIn(ExperimentalPagingApi::class)
   private val pager = Pager(
      pagingConfig,
      0,
      null,
      pagingSourceFactory
   )

   private val _searchList = MutableStateFlow(PagingData.empty<MalAnimeSearchFrameRender>())
   val searchList get() = _searchList.asStateFlow()

   init {
      viewModelScope.launch {
         pager.flow.cachedIn(viewModelScope).catch {
            Log.e("$it", it.message, it)
         }.collectLatest {
            _searchList.value = it.map { malAnime ->
               MalAnimeSearchFrameRender(
                  malAnime.mapToMalListGridItem()
               )
            }
         }
      }
   }


}