package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.InvalidatingPagingSourceFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.db.MalDatabase
import com.example.anyme.domain.ui.ListItem
import com.example.anyme.repositories.IMalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserAnimeListViewModel @Inject constructor(
   private val malRepository: IMalRepository
): ViewModel() {

   init{
      viewModelScope.launch {
         malRepository.retrieveUserAnimeList()
      }
   }

   var orderBy = MalDatabase.OrderBy.LastUpdateAt
      set(value) {
         field = value
         pagingSourceFactory.invalidate()
      }

   var orderDirection = MalDatabase.OrderDirection.Asc
      set(value){
         field = value
         pagingSourceFactory.invalidate()
      }

   var filter = ""
      set(value) {
         field = value
         pagingSourceFactory.invalidate()
      }

   private val pagingSourceFactory = InvalidatingPagingSourceFactory{
      malRepository.fetchMalUserAnime(
         orderBy,
         orderDirection,
         filter
         )
   }

   @OptIn(ExperimentalPagingApi::class)
   val list = Pager(
      PagingConfig(50, 10),
      0,
      null,
      pagingSourceFactory
   ).flow.map { pagingData ->
      pagingData.map { item ->
         item.mapToMalAnimeDL().mapToMalAnimeListItem()
      }
   }.cachedIn(viewModelScope)




}