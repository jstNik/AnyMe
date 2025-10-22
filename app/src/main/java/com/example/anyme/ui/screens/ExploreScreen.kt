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
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.repositories.MalRepository.RankingListType
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.Details
import com.example.anyme.viewmodels.ExploreViewModel

@Composable
fun ExploreScreen(
   navigator: NavHostController,
   contentPadding: PaddingValues,
   viewModel: ExploreViewModel = hiltViewModel<ExploreViewModel>()
) {

   val ranks = RankingListType.entries.map { it.toString() }
   val rowStates = List(viewModel.rankingLists.size) { rememberLazyListState() }
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
            listSize = { viewModel.rankingLists.size },
            getElement = { viewModel.rankingLists.getOrNull(it) },
            key = { it },
            contentPadding = PaddingValues()
         ) { idx, item ->

            val pagingItems = item.collectAsLazyPagingItems()

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
                  count = pagingItems.itemCount,
                  key = { idx ->
                     val item = pagingItems.peek(idx)?.media
                     if (item != null && item.id != 0)
                        item.id
                     else
                        (-idx - 1)
                  }
               ) { rowIdx ->

                  val render = pagingItems[rowIdx]
                  render?.Compose {
                     navigator.navigate("$Details/${render.media.host}/${render.media.id}")
                  }

               }

            }
         }
      }
   }
}