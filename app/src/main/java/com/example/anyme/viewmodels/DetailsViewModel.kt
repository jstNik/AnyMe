package com.example.anyme.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.ui.renders.mal.MalAnimeDetailsRender
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
   private val malRepository: IMalRepository,
   savedStateHandle: SavedStateHandle
): ViewModel() {

   private val _animeDetails = MutableStateFlow<MediaDetailsRender>(MalAnimeDetailsRender())
   val animeDetails get() = _animeDetails.asStateFlow()

   private val mediaId: Int = (savedStateHandle["mediaId"] as Int?)!!

   init{
      fetchAnimeDetails()
   }

   private fun fetchAnimeDetails(){
      viewModelScope.launch{
         malRepository.fetchAnimeDetails(mediaId).catch {
            Log.e("$it", "${it.message}", it)
            // TODO
         }.collect {
            _animeDetails.value = MalAnimeDetailsRender(it.mapToMalAnimeDetails())
            Log.d("Related Anime Size", "${it.relatedAnime.size}")
         }
      }
   }



}