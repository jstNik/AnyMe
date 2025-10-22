package com.example.anyme.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.anyme.viewmodels.DetailsViewModel

@Composable
fun DetailsScreen(
   navigator: NavHostController,
   contentPadding: PaddingValues,
   viewModel: DetailsViewModel = hiltViewModel<DetailsViewModel>()
){

   Column(
      modifier = Modifier
         .fillMaxSize()
         .padding(contentPadding)
   ) {
      val media by viewModel.animeDetails.collectAsStateWithLifecycle()
      media.Compose()
   }

}