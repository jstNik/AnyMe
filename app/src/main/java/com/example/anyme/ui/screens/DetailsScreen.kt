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
import com.example.anyme.utils.Resource
import com.example.anyme.viewmodels.DetailsViewModel

@Composable
fun DetailsScreen(
   contentPadding: PaddingValues,
   viewModel: DetailsViewModel = hiltViewModel<DetailsViewModel>()
){

   val resource by viewModel.mediaDetails.collectAsStateWithLifecycle()

   when(resource.status){
      Resource.Status.Success -> {
         Column(
            modifier = Modifier
               .fillMaxSize()
               .padding(contentPadding)
         ) {
            val media = resource.data!!
            media.Compose()
         }
      }
      Resource.Status.Loading -> { Log.d("UI State", "Details Screen loading") }
      Resource.Status.Failure -> {
         Log.d("UI State", "Details Screen failed")
         Log.e("${resource.error}", "${resource.error?.message}", resource.error)
      }
   }

}