package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.domain.dl.mal.mapToMalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.ui.renders.mal.MalSeasonalAnimeRender
import kotlinx.datetime.DatePeriod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar
import javax.inject.Inject
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SeasonalAnimeListViewModel @Inject constructor(
   private val malRepository: IMalRepository
) : ViewModel() {

   private val _seasonalAnimes = MutableStateFlow(listOf<MalSeasonalAnimeRender>())
   val seasonalAnimes get() = _seasonalAnimes.asStateFlow()

   private val calendar = Calendar.getInstance()
   private val localDate: LocalDateTime
      get() = Instant
         .fromEpochMilliseconds(calendar.timeInMillis)
         .toLocalDateTime(TimeZone.currentSystemDefault())

   private val _today = MutableStateFlow(localDate.date)
   val today get() = _today.asStateFlow()

   init {
      viewModelScope.launch {
         malRepository.retrieveMalSeasonalAnimes().collectLatest { flow ->
            _seasonalAnimes.value = flow.map {
               MalSeasonalAnimeRender(it.mapToMalSeasonalListItem())
            }.filter {
               it.media.getDateTimeNextEp() != null
            }.sortedBy {
               it.media.getDateTimeNextEp()
            }
         }
      }
      viewModelScope.launch {
         while (currentCoroutineContext().isActive) {
            val tomorrow = localDate.date.plus(DatePeriod(days = 1))
            delay(tomorrow.toEpochDays().days - calendar.timeInMillis.milliseconds)
            _today.value = tomorrow
         }
      }
   }
}