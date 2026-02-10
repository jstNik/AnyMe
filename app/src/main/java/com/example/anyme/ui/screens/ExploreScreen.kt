package com.example.anyme.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.ui.composables.SearchBar
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.navigation.Screen
import com.example.anyme.viewmodels.ExploreViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun ExploreScreen(
   viewModel: ExploreViewModel = hiltViewModel<ExploreViewModel>(),
   onNavigation: (Screen) -> Unit
) {

   val lazyItems = viewModel.searchList.collectAsLazyPagingItems()
   val state = rememberLazyGridState()

   if (lazyItems.loadState.refresh !is LoadState.Loading || lazyItems.itemCount > 0) {

      Column(
         modifier = Modifier
            .fillMaxSize()
      ) {


         SearchBar(
            modifier = Modifier
               .fillMaxWidth()
               .padding(all = 16.dp)
         ) {
            delay(1.seconds)
            if (it.length > 2)
               viewModel.setSearchQuery(it)
            else
               viewModel.setSearchQuery("")
         }

         SwipeUpToRefresh(
            scrollableState = state,
            isRefreshing = false,
            onRefresh = { },
         ) { ->

            LazyVerticalGrid(
               columns = GridCells.Adaptive(160.dp),
               state = state,
               contentPadding = PaddingValues(4.dp
               ),
               horizontalArrangement = Arrangement.SpaceBetween,
               modifier = Modifier
                  .fillMaxSize()
                  .background(MaterialTheme.colorScheme.background)
            ) {

               items(
                  count = lazyItems.itemCount,
                  key = {
                     val id = lazyItems.peek(it)?.media?.id
                     if (id != null && id != 0) id else -it - 1
                  }
               ) { idx ->
                  lazyItems[idx]?.let { render ->
                     Row(
                        modifier = Modifier
                           .height(320.dp)
                           .padding(horizontal = 4.dp, vertical = 8.dp),
//                           .height(350.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                     ) {
                        render.Compose {
                           onNavigation(
                              Screen.Details(render.media.id, render.media.host)
                           )
                        }
                     }
                  }
               }
            }
         }
      }
   }
}