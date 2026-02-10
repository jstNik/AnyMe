package com.example.anyme.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import com.example.anyme.remote.Host
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.repositories.RepositoryVisitor
import com.example.anyme.data.visitors.renders.CallbacksBundle
import com.example.anyme.data.visitors.renders.DetailsRenderVisitor
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.ui.navigation.Screen
import com.example.anyme.utils.Resource
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import javax.inject.Inject

@HiltViewModel(assistedFactory = DetailsViewModel.Factory::class)
class DetailsViewModel @AssistedInject constructor(
   @Assisted val mediaId: Int,
   @Assisted val host: Host,
   private val settingsRepo: SettingsRepository,
   private val repositoryVisitor: RepositoryVisitor,
   private val converterVisitor: ConverterVisitor,
   private val renderVisitor: DetailsRenderVisitor
): ViewModel() {

   @AssistedFactory
   interface Factory {
      fun create(mediaId: Int, host: Host): DetailsViewModel
   }

   private val refreshingBehavior = RefreshingBehavior()
   val isRefreshing get() = refreshingBehavior.isRefreshing

   private val _updatingStatus = MutableStateFlow(Resource.success(Unit))

   @OptIn(ExperimentalCoroutinesApi::class)
   val mediaDetails = combine(
      isRefreshing,
      _updatingStatus
   ) { refreshing, updatingStatus ->
      refreshing to updatingStatus
   }.filterNotNull().flatMapLatest { (refreshing, updatingStatus) ->
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
               onRefresh = { refreshingBehavior.refresh() }
            )
         )
         Resource.success(render)
      }.onEach {
         if (refreshing == RefreshingStatus.Refreshing)
            refreshingBehavior.stop()
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