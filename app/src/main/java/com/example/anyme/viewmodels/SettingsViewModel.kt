package com.example.anyme.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.anyme.domain.ui.Settings
import com.example.anyme.data.repositories.SettingsRepository
import com.example.anyme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
   private val settingsRepo: SettingsRepository
): ViewModel() {

   private val _settings = MutableStateFlow(Resource.loading<Settings>())
   val settings = _settings.asStateFlow()

   init { collectSettings() }

   private fun collectSettings(){
      viewModelScope.launch {
         _settings.value = Resource.loading()
         settingsRepo.flow.catch {
            if(it is Exception)
               _settings.value = Resource.failure(it)
            else throw it
         }.collect {
            _settings.value = Resource.success(it)
         }
      }
   }

   fun changeSettings(settings: Settings){
      viewModelScope.launch {
         settingsRepo.changeSettings(settings)
      }
   }

}