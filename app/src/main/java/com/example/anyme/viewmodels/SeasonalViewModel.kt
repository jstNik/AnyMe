package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.domain.dl.mal.mapToMalSeasonalListItem
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.utils.Resource
import com.example.anyme.utils.time.toLocalDataTime
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SeasonalViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: MalRepository,
   private val converterVisitor: ConverterVisitor,
   private val renderVisitor: ListItemRenderVisitor
) : ViewModel() {

   private val refreshingBehavior = RefreshingBehavior()
   val isRefreshing get () = refreshingBehavior.isRefreshing

   @OptIn(ExperimentalCoroutinesApi::class)
   val seasonalMedia = combine(isRefreshing) { (it) ->
      if(it == RefreshingStatus.NotRefreshing) null else it
   }.filterNotNull().flatMapMerge { refreshing ->
      malRepository.fetchSeasonalMedia().map { list ->
         val transform = list.map {
            it.mapToMalSeasonalListItem()
         }.sortedBy {
            it.getDateTimeNextEp()
         }.map {
            it.acceptRender(renderVisitor)
         }
         Resource.success(transform)
      }.onEach {
         if(refreshing != RefreshingStatus.NotRefreshing)
            refreshingBehavior.stop()
      }
   }.onStart {
      emit(Resource.loading())
   }.catch { e ->
      if(e is Exception) emit(Resource.failure(e)) else throw e
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000L),
      Resource.loading()
   )


   val today = flow {

      val calendar = Calendar.getInstance()

      while(currentCoroutineContext().isActive) {
         val now = calendar.timeInMillis.milliseconds
         val tomorrow = (now + 1.days).inWholeDays.days

         delay(tomorrow - now)
         emit(calendar.timeInMillis.toLocalDataTime())
      }
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000L),
      Calendar.getInstance().timeInMillis.toLocalDataTime()
   )

   fun refresh(){
      refreshingBehavior.refresh()
   }

}