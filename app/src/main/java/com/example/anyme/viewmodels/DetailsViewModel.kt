package com.example.anyme.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import com.example.anyme.remote.Host
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.repositories.RepositoryVisitor
import com.example.anyme.data.visitors.renders.CallbacksBundle
import com.example.anyme.data.visitors.renders.DetailsRenderVisitor
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.utils.Resource
import com.example.anyme.utils.Resource.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val savedStateHandle: SavedStateHandle,
   private val repositoryVisitor: RepositoryVisitor,
   private val converterVisitor: ConverterVisitor,
   private val renderVisitor: DetailsRenderVisitor
): ViewModel() {

   companion object{
      const val MEDIA_KEY = "media_id"
      const val HOST_KEY = "host"
   }

   private data class ValueContainer(
      val mediaId: Int,
      val host: Host,
      val refreshing: RefreshingStatus,
      val updatingStatus: Resource<Unit>
   ){

      companion object {
         fun check(
            id: Resource<Int>,
            host: Resource<Host>,
            refreshing: RefreshingStatus,
            updatingStatus: Resource<Unit>
         ): ValueContainer? {
            if (id.status == Status.Failure || host.status == Status.Failure)
               error("Media has no host or no id")
            if (id.status == Status.Loading || host.status == Status.Loading
            )
               return null
            return ValueContainer(id.data!!, host.data!!, refreshing, updatingStatus)
         }
      }

   }

   private val mediaId = savedStateHandle.getStateFlow(MEDIA_KEY, 0).map{
      if(it != 0) Resource.success(it) else Resource.failure(
         IllegalStateException("Media id is unknown")
      )
   }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = Resource.loading()
   )
   private val host = savedStateHandle.getStateFlow(HOST_KEY, "").map {
      val enum = Host.getEnum(it)
      if(enum != Host.Unknown) Resource.success(enum) else Resource.failure(
         IllegalStateException("Host is unknown")
      )
   }.stateIn(
      scope = viewModelScope,
      started = SharingStarted.WhileSubscribed(5000L),
      initialValue = Resource.loading()
   )

   private val _updatingStatus = MutableStateFlow(Resource.success(Unit))

   private val _isRefreshing = MutableStateFlow(RefreshingStatus.InitialRefreshing)

   private val triggerRefresh = MutableStateFlow(Unit)

   init{
      combine(mediaId, host){ id, host ->
         ValueContainer.check(id, host, RefreshingStatus.InitialRefreshing, Resource.success(Unit))
      }.filterNotNull().onEach {
         _isRefreshing.value = it.refreshing
      }.launchIn(viewModelScope)
   }

   @OptIn(ExperimentalCoroutinesApi::class)
   val mediaDetails = combine(
      mediaId,
      host,
      _isRefreshing,
      _updatingStatus,
      triggerRefresh
   ) { mediaId, host, refreshing, updatingStatus, _ ->
      ValueContainer.check(mediaId, host, refreshing, updatingStatus)
   }.filterNotNull().flatMapLatest { (mediaId, host, refreshing, updatingStatus) ->
      var media = when (host) {
         Host.Mal -> {
            MalAnime(id = mediaId)
         }
//         Host.TheMovieDatabase -> { }
         else -> error("Media has no host or no id")
      }

      media.acceptRepository(repositoryVisitor) { repo, item ->
         repo.fetchMediaDetails(item, refreshing)
      }.map { mediaDomain ->
         val mediaUi = mediaDomain.acceptConverter(converterVisitor) { mapper ->
            mapper.mapDomainToDetails()
         }
         val render = mediaUi.acceptRender(
            renderVisitor,
            CallbacksBundle(
               isRefreshing = refreshing == RefreshingStatus.Refreshing,
               updatingStatus = updatingStatus,
               onSave = { update(it) },
               onRefresh = { refresh() }
            )
         )
         Resource.success(render)
      }.onEach {
         if (_isRefreshing.value == RefreshingStatus.Refreshing)
            _isRefreshing.value = RefreshingStatus.NotRefreshing
      }
   }.onStart {
      emit(Resource.loading())
   }.catch {
      if(it is Exception) emit(Resource.failure(it)) else throw it
   }.stateIn(
      viewModelScope,
      SharingStarted.WhileSubscribed(5000L),
      Resource.loading()
   )

   fun refresh(){
      triggerRefresh.value = Unit
      _isRefreshing.value = RefreshingStatus.Refreshing
   }

   fun update(media: MediaUi) {
      _updatingStatus.value = Resource.loading()
      try {
         media.acceptConverter(converterVisitor) {
            it.mapDetailsToDomain()
         }.acceptRepository(repositoryVisitor) { repo, item ->
            viewModelScope.launch {
               try {
                  repo.update(item)
                  _updatingStatus.value = Resource.success(Unit)
               } catch(e: Exception){
                  _updatingStatus.value = Resource.failure(e)
               }
            }
         }
      } catch(e: NullPointerException){
         _updatingStatus.value = Resource.failure(e)
      }
   }

}