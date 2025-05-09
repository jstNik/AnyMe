package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.domain.ui.MalSeasonalListItem
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SeasonalAnimeListViewModel @Inject constructor(
   private val malRepository: IMalRepository
): ViewModel() {

   private val _seasonalAnimes = MutableStateFlow(listOf<MalSeasonalListItem>())
   val seasonalAnimes get() = _seasonalAnimes.asStateFlow()

   init {
      viewModelScope.launch {
         malRepository.retrieveMalSeasonalAnimes().collect {
            _seasonalAnimes.value = it.sortedBy { it.nextEp.releaseDate }
         }
      }
   }

}