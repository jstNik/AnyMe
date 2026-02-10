package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.domain.dl.mal.mapToMalSeasonalListItem
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.utils.Resource
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.time.toLocalDataTime
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.datetime.DayOfWeek
import java.util.Calendar
import javax.inject.Inject
import kotlin.collections.sorted
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SeasonalsViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: MalRepository,
   private val converterVisitor: ConverterVisitor,
   private val renderVisitor: ListItemRenderVisitor
) : ViewModel() {

   private data class SeasonalMediaParams(
      val weekDay: DayOfWeek,
      val isRefreshingStatus: RefreshingStatus,
      val onlyInMyList: Boolean
   )

   private val refreshingBehavior = RefreshingBehavior()
   val isRefreshing get () = refreshingBehavior.isRefreshing

   private val _onlyInMyList = MutableStateFlow(false)
   val onlyInMyList get() = _onlyInMyList.asStateFlow()

   private val _weekDaySelected = MutableStateFlow(
      Calendar.getInstance().timeInMillis.toLocalDataTime().dayOfWeek
   )

   private val _cache = MutableStateFlow(emptyList<MalSeasonalListItem>())

   @OptIn(ExperimentalCoroutinesApi::class)
   val seasonalMedia = combine(
      _weekDaySelected,
      isRefreshing,
      _onlyInMyList
   ) { weekDay, refreshing, onlyInMyList ->
      SeasonalMediaParams(weekDay, refreshing, onlyInMyList)
   }.filterNotNull().flatMapLatest { (weekDay, refreshing, onlyInMyList) ->
      if (refreshing != RefreshingStatus.NotRefreshing) {
         malRepository.fetchSeasonalMedia().map { seasonals ->
            seasonals.map {
               it.mapToMalSeasonalListItem()
            }
         }.onEach {
            _cache.value = it
            refreshingBehavior.stop()
         }
      } else {
         _cache
      }.map {
         val renders: List<MediaListItemRender.OffsetDateTimeComparable> = it.filter { seasonal ->
            val f1 = seasonal.dateTimeNextEp?.dateTime?.dayOfWeek == weekDay
            if(onlyInMyList) f1 && seasonal.listStatus != MyList.Status.Unknown else f1
         }.map { seasonal ->
            seasonal.acceptRender(renderVisitor)
         }.sorted()
         Resource.success(renders)
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

   fun selectWeekDay(weekDay: DayOfWeek){
      _weekDaySelected.value = weekDay
   }

   fun onCheckedChanged(value: Boolean){
      _onlyInMyList.value = value
   }

}