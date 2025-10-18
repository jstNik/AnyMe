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
import com.example.anyme.local.db.MalDatabase
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.mapToMalAnimeListItem
import com.example.anyme.domain.local.mal.mapToMalAnimeDL
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.ui.renders.mal.MalUserListAnimeRender
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class UserListViewModel @Inject constructor(
   private val malRepository: IMalRepository,
   private val gson: Gson
): ViewModel() {

   var myListStatus = MyList.Status.Watching
      set(value) {
         field = value
         pagingSourceFactory.invalidate()
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
         myListStatus,
         orderBy,
         orderDirection,
         filter
      )
   }

   private val pager = Pager(
      PagingConfig(50, 10),
      0,
      null,
      pagingSourceFactory
   )

   private val _list = MutableStateFlow<PagingData<MalUserListAnimeRender>>(PagingData.empty())
   val list get() = _list.asStateFlow()

   init{
      viewModelScope.launch {
         malRepository.retrieveUserAnimeList()
      }

      viewModelScope.launch{
         pager.flow.map { pagingData ->
            pagingData.map { item ->
               val malAnimes = item.mapToMalAnimeDL(gson).mapToMalAnimeListItem()
               MalUserListAnimeRender(malAnimes)
            }
         }.cachedIn(viewModelScope).catch{
            Log.e("$it", it.message, it)
         }.collectLatest {
            _list.value = it
         }
      }

   }

//   @OptIn(ExperimentalPagingApi::class)
//   val listFlow = Pager(
//      PagingConfig(50, 10),
//      0,
//      null,
//      pagingSourceFactory
//   ).flow.map { pagingData ->
//      pagingData.map { item ->
//         item.mapToMalAnimeDL(gson).mapToMalAnimeListItem()
//      }
//   }.cachedIn(viewModelScope)




}