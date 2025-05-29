package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.domain.ui.MalSeasonalListItem
import com.example.anyme.repositories.IMalRepository
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

   private val _seasonalAnimes = MutableStateFlow(listOf<MalSeasonalListItem>())
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
         malRepository.retrieveMalSeasonalAnimes().collectLatest {
            _seasonalAnimes.value = it
               .filter { it.getDateTimeNextEp() != null }
               .sortedBy { it.getDateTimeNextEp() }
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