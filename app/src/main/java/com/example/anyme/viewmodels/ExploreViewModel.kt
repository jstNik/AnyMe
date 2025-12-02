package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.data.visitors.repositories.RepositoryVisitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import kotlin.collections.map
import kotlin.to

@HiltViewModel
open class ExploreViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository,
   private val malRepository: MalRepository,
   private val repositoryVisitor: RepositoryVisitor,
   private val converterVisitor: ConverterVisitor,
   private val renderVisitor: ListItemRenderVisitor
//   private val malRepository: Repository
) : ViewModel() {


   val rankingListFlow = MalRepository.MalRankingTypes.entries.map { type ->
      type to malRepository.fetchRankingLists(type).map {
         it.map { data ->
            data.acceptConverter(converterVisitor) { mapper ->
               mapper.mapDomainToRankingListItem().acceptRender(renderVisitor)
            }
         }
      }.stateIn(
         scope = viewModelScope,
         started = SharingStarted.WhileSubscribed(5000L),
         initialValue = PagingData.empty()
      ).cachedIn(viewModelScope)
   }

}