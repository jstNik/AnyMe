package com.example.anyme.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.remote.Host
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.SettingsRepository
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.ui.renders.mal.MalAnimeDetailsRender
import com.example.anyme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: IMalRepository,
   savedStateHandle: SavedStateHandle
): ViewModel() {


   private val mediaId: Int = checkNotNull(savedStateHandle["media_id"] as Int?)
   private val host: Host = Host.valueOf(checkNotNull(savedStateHandle["host"] as String?))

   private val flow = malRepository.fetchAnimeDetails(mediaId)

   val animeDetails = flow.map {
      Resource.success(MalAnimeDetailsRender(it.mapToMalAnimeDetails()))
   }.onStart {
      emit(Resource.loading())
   }.catch { e ->
      if(e is Exception) emit(Resource.failure(e)) else throw e
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000L),
      Resource.loading()
   )



   init {
      viewModelScope.launch {
         malRepository.synchronizeApiWithDB(flow.first())
      }
   }

}