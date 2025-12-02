package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.domain.dl.mal.mapToMalSeasonalListItem
import com.example.anyme.data.repositories.Repository
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.ui.renders.mal.MalSeasonalAnimeRender
import com.example.anyme.utils.Resource
import com.example.anyme.utils.time.toLocalDataTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
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
   private val renderVisitor: ListItemRenderVisitor
) : ViewModel() {

   val seasonalAnimes = malRepository.fetchSeasonalMedia().map { list ->
      val transform = list.map{
         it.mapToMalSeasonalListItem().acceptRender(renderVisitor)
      }
      Resource.success(transform)
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

         delay(tomorrow - now + 1.milliseconds)
         emit(calendar.timeInMillis.toLocalDataTime())
      }
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000L),
      Calendar.getInstance().timeInMillis.toLocalDataTime()
   )

}