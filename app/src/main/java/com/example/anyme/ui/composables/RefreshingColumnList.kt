package com.example.anyme.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import com.example.anyme.domain.ui.ListItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RefreshingColumnList(
   list: LazyPagingItems<ListItem>,
   lazyColumnState: LazyListState,
   key: ((Int) -> Any)? = null,
   contentPadding: PaddingValues = PaddingValues(),
   onRefresh: () -> Unit = { },
   onSwipeStartToEnd: (listItem: ListItem) -> Unit = { },
   onSwipeEndToStart: (listItem: ListItem) -> Unit = { },
   onClick: (listItem: ListItem) -> Unit = { }
) {

   var isRefreshing by remember { mutableStateOf(false) }
   val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
   val isSwipeEnabled = remember {
      derivedStateOf {
         !lazyColumnState.canScrollBackward && !lazyColumnState.isScrollInProgress
            || swipeRefreshState.isSwipeInProgress
      }
   }

   LaunchedEffect(isRefreshing) {
      if (isRefreshing) {
         onRefresh.invoke()
         isRefreshing = false
      }
   }

   SwipeRefresh(
      state = swipeRefreshState,
      swipeEnabled = isSwipeEnabled.value,
      onRefresh = {
         isRefreshing = true
      },
   ) {

      LazyColumn(
         state = lazyColumnState,
         contentPadding = contentPadding,
         modifier = Modifier.background(Color.Transparent)
      ) {

         items(
            count = list.itemCount,
            key = key
         ) {

            list[it]?.let { item ->

               item.Render(
                  Modifier
                     .animateItem(/* TODO */)
                     .clickable { onClick.invoke(item) },
                  onClick = { }
               )
            }
         }
      }
   }
}