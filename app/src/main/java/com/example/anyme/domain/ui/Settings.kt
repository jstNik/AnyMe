package com.example.anyme.domain.ui

import androidx.compose.runtime.Immutable
import com.example.anyme.remote.Host

@Immutable
data class Settings(
   val host: Host
){

   companion object{
      val DEFAULT = Settings(Host.Mal)
   }

}