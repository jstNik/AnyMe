package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.ConverterVisitor
import com.example.anyme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: MalRepository,
   private val converterVisitor: ConverterVisitor
) : ViewModel() {


   private val _searchQuery = MutableStateFlow("")

   fun setSearchQuery(searchQuery: String){
      _searchQuery.value = searchQuery
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val searchList = combine(_searchQuery) { (searchQuery) ->
      searchQuery
   }.distinctUntilChanged().flatMapLatest {
      malRepository.search(it)
   }.map { pagingData ->
      pagingData.map { data ->
         data.acceptConverter(converterVisitor) { it.mapDomainToGridItem() }
      }
   }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = PagingData.empty()
   ).cachedIn(viewModelScope)




}