package com.example.anyme.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.data.repositories.MalRepository.MalRankingTypes
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.Details
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.viewmodels.ExploreViewModel

@Composable
fun ExploreScreen(
   contentPadding: PaddingValues,
   viewModel: ExploreViewModel = hiltViewModel<ExploreViewModel>()
) {

   val navigator = LocalNavHostController.current
   val rankingLists = viewModel.rankingListFlow

   val ranks = MalRankingTypes.entries.map { it.toString() }
   val rowStates = List(rankingLists.size) { rememberLazyListState() }
   val lazyColumState = rememberLazyListState()

   Column(
      modifier = Modifier
         .fillMaxSize()
         .padding(contentPadding)
   ) {

      SwipeUpToRefresh(
         lazyColumState
      ) { scrollableState ->

         LazyColumnList(
            lazyColumnState = scrollableState,
            listSize = { rankingLists.size },
            getElement = { rankingLists.getOrNull(it)?.second },
            key = { it },
            contentPadding = PaddingValues()
         ) { idx, pagingData ->

            val lazyItems = pagingData.collectAsLazyPagingItems()

            if (lazyItems.loadState.refresh !is LoadState.Loading || lazyItems.itemCount > 0) {

               Text(
                  ranks.getOrNull(idx) ?: "",
                  style = MaterialTheme.typography.titleMedium
               )

               LazyRow(
                  state = rowStates.getOrNull(idx) ?: rememberLazyListState(),
                  modifier = Modifier
                     .fillMaxWidth()
               ) {

                  items(
                     count = lazyItems.itemCount,
                     key = { idx ->
                        val item = lazyItems.peek(idx)?.media
                        if (item != null && item.id != 0)
                           item.id
                        else
                           (-idx - 1)
                     }
                  ) { rowIdx ->

                     val render = lazyItems[rowIdx]
                     render?.Compose {
                        val route = "$Details/${render.media.host}/${render.media.id}"
                        navigator.navigate(route)
                     }

                  }

               }
            }
         }
      }
   }
}
