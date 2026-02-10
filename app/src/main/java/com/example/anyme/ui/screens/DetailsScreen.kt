package com.example.anyme.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anyme.remote.Host
import com.example.anyme.ui.navigation.Screen
import com.example.anyme.utils.Resource
import com.example.anyme.viewmodels.DetailsViewModel

@Composable
fun DetailsScreen(
   id: Int,
   host: Host,
   viewModel: DetailsViewModel = hiltViewModel<DetailsViewModel, DetailsViewModel.Factory>(
      creationCallback = {
         it.create(id, host)
      }
   ),
   onNavigation: (Screen) -> Unit
){

   val resource by viewModel.mediaDetails.collectAsStateWithLifecycle()

   when(resource.status){
      Resource.Status.Success -> {
         Column(
            modifier = Modifier
               .fillMaxSize()
         ) {
            val media = resource.data!!
            media.Compose(onNavigation)
         }
      }
      Resource.Status.Loading -> { Log.d("UI State", "Details Screen loading") }
      Resource.Status.Failure -> {
         Log.d("UI State", "Details Screen failed")
         Log.e("${resource.error}", "${resource.error?.message}", resource.error)
      }
   }

}