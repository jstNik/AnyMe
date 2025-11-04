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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.repositories.MalRepository.RankingListType
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.Details
import com.example.anyme.utils.Resource
import com.example.anyme.viewmodels.ExploreViewModel
import kotlinx.coroutines.flow.flowOf

@Composable
fun ExploreScreen(
   navigator: NavHostController,
   contentPadding: PaddingValues,
   viewModel: ExploreViewModel = hiltViewModel<ExploreViewModel>()
) {

   val resource by viewModel.rankingListFlow.collectAsStateWithLifecycle()

   val ranks = RankingListType.entries.map { it.toString() }
   val rowStates = List(resource.size) { rememberLazyListState() }
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
            listSize = { resource.size },
            getElement = { resource.getOrNull(it)?.second },
            key = { it },
            contentPadding = PaddingValues()
         ) { idx, item ->

            when(item.status){
               Resource.Status.Success -> {
                  val pagingItems = flowOf(item.data!!).collectAsLazyPagingItems()

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
               Resource.Status.Loading -> { /* TODO */ }
               Resource.Status.Failure -> { /* TODO */ }
            }
         }
      }
   }
}