package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.repositories.MalRepository.RankingListType
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.viewmodels.RankingListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RankingListActivity : AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContent {
         AnyMeTheme(darkTheme = true) {
            ComposeRankingLists()
         }
      }

   }


}

@Composable
fun ComposeRankingLists(viewModel: RankingListViewModel = viewModel()) {
   Scaffold(
      topBar = {

      },
      bottomBar = {

      }
   ) { contentPadding ->

      val ranks = RankingListType.entries.map { it.toString() }
      val rowStates = List(viewModel.rankingLists.size) { rememberLazyListState() }
      val lazyColumState = rememberLazyListState()


      SwipeUpToRefresh(
         lazyColumState
      ) { scrollableState ->

         LazyColumnList(
            lazyColumnState = scrollableState,
            listSize = { viewModel.rankingLists.size },
            getElement = { viewModel.rankingLists.getOrNull(it) },
            key = { it },
            contentPadding = contentPadding
         ) { idx, item ->

            val pagingItems = item.collectAsLazyPagingItems()

            Text(
               ranks.getOrNull(idx)?.toString() ?: "",
               style = MaterialTheme.typography.titleMedium
            )

            LazyRow(
               state = rowStates.getOrNull(idx) ?: rememberLazyListState(),
               modifier = Modifier
                  .fillMaxWidth()
            ) {

               items(
                  count = pagingItems.itemCount,
                  key = {
                     val item = pagingItems.peek(it)?.media
                     if (item != null && item.id != 0)
                        item.id
                     else
                        (-it - 1)
                  }
               ) { rowIdx ->

                  pagingItems[rowIdx]?.Compose{

                  }

               }

            }
         }

      }
   }
}