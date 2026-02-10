package com.example.anyme.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.CombinedLoadStates
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.MalRepository.MalRankingTypes
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.data.visitors.repositories.RepositoryVisitor
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.internal.toImmutableMap
import javax.inject.Inject
import kotlin.collections.map
import kotlin.to

@HiltViewModel
open class RankingsViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: MalRepository,
   private val repositoryVisitor: RepositoryVisitor,
   private val converterVisitor: ConverterVisitor,
   private val renderVisitor: ListItemRenderVisitor
//   private val malRepository: Repository
) : ViewModel() {

   private val refreshingBehaviors = MalRankingTypes.entries.associateWith { RefreshingBehavior() }

   @OptIn(ExperimentalCoroutinesApi::class)
   val refreshStates = refreshingBehaviors.mapValues {
      it.value.isRefreshing.mapLatest { refreshingStatus ->
         refreshingStatus == RefreshingStatus.Refreshing
      }.stateIn(
         viewModelScope,
         SharingStarted.WhileSubscribed(5000L),
         false
      )
   }

   private val _rankingOnScreen = MutableStateFlow(emptySet<MalRankingTypes>())

   private val _rankingOnScreenCache = MutableStateFlow(emptySet<MalRankingTypes>())

   @OptIn(ExperimentalCoroutinesApi::class)
   val isRefreshing = _rankingOnScreenCache.flatMapLatest { rankingOnScreenCache ->

      if (rankingOnScreenCache.isEmpty())
         return@flatMapLatest flowOf(false)

      combine(rankingOnScreenCache.mapNotNull { refreshingBehaviors[it]?.isRefreshing }) { refreshingList ->
         when {
            refreshingList.any { it == RefreshingStatus.Refreshing } -> RefreshingStatus.Refreshing
            refreshingList.any { it == RefreshingStatus.InitialRefreshing } -> RefreshingStatus.InitialRefreshing
            else -> RefreshingStatus.NotRefreshing
         }

      }.onEach {
         if (it == RefreshingStatus.NotRefreshing)
            _rankingOnScreenCache.value = emptySet()
      }.map {
         it == RefreshingStatus.Refreshing
      }
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000L),
      false
   )


   @OptIn(ExperimentalCoroutinesApi::class)
   val rankingListFlow = MalRankingTypes.entries.map { type ->
      type to malRepository.fetchRankingLists(type).map { pagingData ->
         pagingData.map { data ->
            data.acceptConverter(converterVisitor) { mapper ->
               mapper.mapDomainToRankingListItem().acceptRender(renderVisitor)
            }
         }
      }.cachedIn(viewModelScope)
   }

   fun refresh(){
      refreshingBehaviors.forEach { it.value.refresh() }
      _rankingOnScreenCache.value = _rankingOnScreen.value
   }

   fun onDataArrival(type: MalRankingTypes){
      refreshingBehaviors[type]?.stop()
   }

   fun onAttach(type: MalRankingTypes){
      _rankingOnScreen.value += type
      if (_rankingOnScreenCache.value.isNotEmpty()) {
         _rankingOnScreenCache.value += type
      }
   }

   fun onDetach(type: MalRankingTypes){
      _rankingOnScreen.value -= type
      if (_rankingOnScreenCache.value.isNotEmpty()) {
         _rankingOnScreenCache.value -= type
      }
   }
}
