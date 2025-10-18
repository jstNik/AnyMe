package com.example.anyme.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.viewmodels.DetailsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailsActivity: AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)

      try {
         enableEdgeToEdge()
         setContent {
            AnyMeTheme {
               ComposeMediaDetailsScreen()
            }
         }
      } catch(e: Exception){
         Log.e("$e", "${e.message}", e)
         finish()
      }
   }


}

@Composable
fun ComposeMediaDetailsScreen(viewModel: DetailsViewModel = viewModel<DetailsViewModel>()){

   val media by viewModel.animeDetails.collectAsStateWithLifecycle()
   media.Compose()

}