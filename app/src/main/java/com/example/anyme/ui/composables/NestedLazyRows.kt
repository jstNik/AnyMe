package com.example.anyme.ui.composables

import android.graphics.fonts.FontStyle
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.domain.ui.MalRankingListItem
import kotlinx.coroutines.flow.Flow

@Composable
fun NestedLazyRows(
   titles: List<String>,
   lists: List<Flow<PagingData<MalRankingListItem>>>,
   rowsState: List<LazyListState>,
   modifier: Modifier = Modifier,
   columnState: LazyListState = rememberLazyListState(),
   contentPadding: PaddingValues = PaddingValues(),
){

   val pagingItems = lists[0].collectAsLazyPagingItems()


   LazyColumn(
      state = columnState,
      contentPadding = contentPadding,
      modifier = Modifier
         .fillMaxSize()
         .then(modifier)
   ) {

      items(
         lists.size,
         key = { titles.getOrNull(it) ?: 0}
         ) { colIdx ->

         val pagingItems = lists[colIdx].collectAsLazyPagingItems()

         Text(
            titles.getOrNull(colIdx)?.toString() ?: "",
            style = MaterialTheme.typography.titleMedium
         )

         LazyRow(
            state = rowsState.getOrNull(colIdx) ?: rememberLazyListState(),
            modifier = Modifier.fillMaxWidth()
         ) {

            items(
               pagingItems.itemCount,
               { pagingItems[it]?.id ?: 0 }
            ){ rowIdx ->

               pagingItems[rowIdx]?.Render(
                  modifier = Modifier.padding(8.dp),
                  onClick = { }
               )

            }

         }

      }

   }

}