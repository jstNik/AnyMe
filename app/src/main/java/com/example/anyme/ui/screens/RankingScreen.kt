package com.example.anyme.ui.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.MalRepository.MalRankingTypes
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.navigation.Screen
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.theme.cs
import com.example.anyme.ui.theme.typo
import com.example.anyme.viewmodels.RankingsViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Composable
fun RankingScreen(
   viewModel: RankingsViewModel = hiltViewModel<RankingsViewModel>(),
   onNavigation: (Screen) -> Unit
) {

   val rankingLists = viewModel.rankingListFlow

   val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
   val lazyColumState = rememberLazyListState()

   SwipeUpToRefresh(
      scrollableState = lazyColumState,
      isRefreshing = isRefreshing,
      onRefresh = {
         viewModel.refresh()
      },
      modifier = Modifier
         .fillMaxSize()
   ) {

      LazyColumnList(
         lazyColumnState = lazyColumState,
         listSize = { rankingLists.size },
         getElement = { rankingLists.getOrNull(it) },
         key = { it },
         contentPadding = PaddingValues()
      ) { idx, pair ->

         if (idx == 0) {
            Row(
               modifier = Modifier
                  .fillMaxWidth(),
               horizontalArrangement = Arrangement.Center
            ) {
               Text(
                  text = "Rankings",
                  style = typo.displaySmall,
                  color = cs.tertiary
               )
            }
         }

         viewModel.refreshStates[pair.first]?.let{
            RankingRow(
               type = pair.first,
               pagingData = pair.second,
               refreshingState = it,
               onNavigation = onNavigation,
               onDetach = { type ->
                  viewModel.onDetach(type)
               },
               onAttach = { type ->
                  viewModel.onAttach(type)
               },
               onDataArrival = { type ->
                  viewModel.onDataArrival(type)
               },
               modifier = Modifier.padding(8.dp)
            )
         }
      }
   }
}

@Composable
fun RankingRow(
   type: MalRankingTypes,
   pagingData: Flow<PagingData<MediaListItemRender>>,
   modifier: Modifier = Modifier,
   refreshingState: StateFlow<Boolean>,
   rowState: LazyListState = rememberLazyListState(),
   onNavigation: (Screen) -> Unit,
   onDataArrival: (MalRankingTypes) -> Unit,
   onDetach: (MalRankingTypes) -> Unit,
   onAttach: (MalRankingTypes) -> Unit
) {

   val lazyItems = pagingData.collectAsLazyPagingItems()
   val isRefreshing by refreshingState.collectAsStateWithLifecycle()

   LaunchedEffect(type) {
      onAttach(type)
   }

   LaunchedEffect(type, lazyItems.loadState.refresh) {
      if(lazyItems.loadState.refresh !is LoadState.Loading)
         onDataArrival(type)
   }

   LaunchedEffect(isRefreshing) {
      if(isRefreshing) {
         lazyItems.refresh()
      }
   }

   DisposableEffect(type) {
      onDispose {
         onDetach(type)
      }
   }

   Card(
      modifier = modifier,
      shape = RoundedCornerShape(32.dp),
      colors = CardDefaults.cardColors(
         containerColor = cs.surfaceContainer
      )
   ) {

      Text(
         type.toString(),
         style = MaterialTheme.typography.titleLarge.copy(
            color = cs.tertiary,
            fontWeight = FontWeight.Bold
         ),
         modifier = Modifier
            .padding(start = 32.dp, top = 16.dp, bottom = 16.dp)
      )

      LazyRow(
         state = rowState,
         modifier = Modifier
            .fillMaxWidth()
            .height(290.dp)
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

            if (rowIdx == 0)
               Spacer(modifier = Modifier.width(16.dp))

            val render = lazyItems[rowIdx]
            render?.Compose {
               onNavigation(
                  Screen.Details(render.media.id, render.media.host)
               )
            }
            Spacer(modifier = Modifier.width(16.dp))
         }
      }
      Spacer(modifier = Modifier.height(16.dp))
   }

}
