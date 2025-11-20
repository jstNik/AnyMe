package com.example.anyme.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.remote.Host
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.ConverterVisitor
import com.example.anyme.data.visitors.RepositoryAcceptor
import com.example.anyme.data.visitors.RepositoryVisitor
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val savedStateHandle: SavedStateHandle,
   private val repositoryVisitor: RepositoryVisitor,
   private val converterVisitor: ConverterVisitor
): ViewModel() {

   private val mediaId = savedStateHandle.getStateFlow("media_id", 0)
   private val host = savedStateHandle.getStateFlow("host", "").map {
      Host.getEnum(it)
   }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = Host.Unknown
   )

   @OptIn(ExperimentalCoroutinesApi::class)
   val animeDetails = combine(mediaId, host){ mediaId, host ->
      mediaId to host
   }.flatMapLatest { (mediaId, host) ->
      var media: RepositoryAcceptor<*, *, *, *> = when(host) {
         Host.Mal -> {
             MalAnime(id = mediaId)
         }
//         Host.TheMovieDatabase -> { }
         else -> {
            if (mediaId == 0) return@flatMapLatest emptyFlow()
            else return@flatMapLatest flow { IllegalStateException("Media has no host") }
         }
      }
      media.acceptRepository(repositoryVisitor) { repo ->
         repo.fetchMediaDetails()
      }
   }.distinctUntilChanged { l, r ->
      val left = l.acceptConverter(converterVisitor) {
         it.mapDomainToDetails().media
      }
      val right = r.acceptConverter(converterVisitor) {
         it.mapDomainToDetails().media
      }
      left == right
   }.map {
      Resource.success(
         it.acceptConverter(converterVisitor) { mapper ->
            mapper.mapDomainToDetails()
         }
      )
   }.onStart {
      emit(Resource.loading())
   }.catch { e ->
      if (e is Exception) emit(Resource.failure(e)) else throw e
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000L),
      Resource.loading()
   )

}