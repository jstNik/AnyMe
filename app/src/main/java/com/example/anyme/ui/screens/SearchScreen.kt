package com.example.anyme.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.ui.composables.SearchBar
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.ui.theme.Pages.Companion.DETAILS
import com.example.anyme.viewmodels.SearchViewModel
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

@Composable
fun SearchScreen(
   contentPadding: PaddingValues,
   viewModel: SearchViewModel = hiltViewModel<SearchViewModel>()
) {

   val navigator = LocalNavHostController.current
   val lazyItems = viewModel.searchList.collectAsLazyPagingItems()

   if (lazyItems.loadState.refresh !is LoadState.Loading || lazyItems.itemCount > 0) {

      Column(
         modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
      ) {

         SearchBar(
            modifier = Modifier.fillMaxWidth()
         ) {
            delay(1.seconds)
            if (it.length > 2)
               viewModel.setSearchQuery(it)
            else
               viewModel.setSearchQuery("")
         }

         val state = rememberLazyGridState()

         SwipeUpToRefresh(
            scrollableState = state,
            isRefreshing = false,
            onRefresh = { },
         ) { ->

            LazyVerticalGrid(
               columns = GridCells.FixedSize(150.dp),
               state = state,
               horizontalArrangement = Arrangement.SpaceEvenly,
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
                     render.Compose {
                        navigator.navigate("$DETAILS/${render.media.host}/${render.media.id}")
                     }
                  }
               }
            }
         }
      }
   }
}