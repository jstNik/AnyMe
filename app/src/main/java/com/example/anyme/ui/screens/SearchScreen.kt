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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.ui.composables.SearchBar
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.Details
import com.example.anyme.utils.Resource
import com.example.anyme.viewmodels.SearchViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlin.time.Duration.Companion.seconds

@Composable
fun SearchScreen(
   navigator: NavHostController,
   contentPadding: PaddingValues,
   viewModel: SearchViewModel = hiltViewModel<SearchViewModel>()
){

   val resource by viewModel.searchList.collectAsStateWithLifecycle()

   when(resource.status){
      Resource.Status.Success -> {
         Column(
            modifier = Modifier
               .fillMaxSize()
               .padding(contentPadding)
         ) {

            SearchBar(
               modifier = Modifier.fillMaxWidth()
            ) {
               delay(1.seconds)
               if(it.length > 2)
                  viewModel.setSearchQuery(it)
               else
                  viewModel.setSearchQuery("")
            }

            val state = rememberLazyGridState()
            val searchList = flowOf(resource.data!!).collectAsLazyPagingItems()

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

                  items (
                     count = searchList.itemCount,
                     key = {
                        val id = searchList.peek(it)?.media?.id
                        if(id != null && id != 0) id else -it - 1
                     }
                  ){ idx ->

                     searchList[idx]?.let { render ->
                        render.Compose {
                           navigator.navigate("$Details/${render.media.host}/${render.media.id}")
                        }
                     }

                  }

               }

            }

         }
      }
      Resource.Status.Loading -> { /* TODO */ }
      Resource.Status.Failure -> { /* TODO */ }
   }

}