package com.example.anyme.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.anyme.domain.ui.Settings
import com.example.anyme.remote.Host
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsRepository (
   private val applicationContext: Context
) {

   companion object {
      private val defaultHost = stringPreferencesKey("default_host")

   }

   val flow = applicationContext.dataStore.data.map { prefs ->
      Settings(
         Host.valueOf(prefs[defaultHost]!!)
      )
   }.catch { e ->
      Log.e("$e", "${e.message}", e)
   }

   suspend fun changeSettings(settings: Settings){
      applicationContext.dataStore.edit { prefs ->
         prefs[defaultHost] = settings.host.toString()
      }
   }

}