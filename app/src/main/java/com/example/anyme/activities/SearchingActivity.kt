package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.ui.composables.SearchBar
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.viewmodels.SearchingViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@AndroidEntryPoint
class SearchingActivity: AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContent{
         AnyMeTheme {
            ComposeSearchingActivity()
         }
      }
   }

}

@Composable
fun ComposeSearchingActivity(viewModel: SearchingViewModel = viewModel()){

   Column(
      modifier = Modifier.fillMaxSize()
   ){

      SearchBar(
         modifier = Modifier.fillMaxWidth()
      ) {
         delay(1.seconds)
         if(it.length > 2)
            viewModel.searchQuery = it
         else
            viewModel.searchQuery = ""
      }

      val state = rememberLazyGridState()
      val searchList = viewModel.searchList.collectAsLazyPagingItems()

      SwipeUpToRefresh(
         scrollableState = state,
         onRefresh = { },
      ) { scrollableState ->

         LazyVerticalGrid(
            columns = GridCells.FixedSize(150.dp),
            state = scrollableState,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
         ) {

            items(
               count = searchList.itemCount,
               key = {
                  val id = searchList.peek(it)?.media?.id
                  if(id != null && id != 0) id else -it - 1
               }
            ){

               searchList[it]?.let{
                  it.Compose {

                  }
               }

            }

         }

      }

   }

}