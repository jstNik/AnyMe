package com.example.anyme.ui.composables

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.anyme.domain.ui.ListItem
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun <T>RefreshingColumnList(
   lazyColumnState: LazyListState,
   listSize: Int,
   getElement: @Composable (idx: Int) -> T?,
   content: @Composable LazyItemScope.(T) -> Unit,
   key: ((Int) -> Any)? = null,
   enableSwipeStartToEnd: Boolean = false,
   enableSwipeEndToStart: Boolean = false,
   swipeStartToEndContent: @Composable RowScope.() -> Unit = { },
   swipeEndToStartContent: @Composable RowScope.() -> Unit = { },
   contentPadding: PaddingValues = PaddingValues(),
   onRefresh: () -> Unit = { },
   onSwipeStartToEnd: (listItem: T) -> Unit = { },
   onSwipeEndToStart: (listItem: T) -> Unit = { },
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
            listSize,
            key
         ) {

            getElement(it)?.let { item ->
               var isSwiped by remember { mutableStateOf(false) }
               val swipeToDismissState = rememberSwipeToDismissBoxState(
                  confirmValueChange = { _ ->
                     isSwiped = true
                     true
                  }
               )

               LaunchedEffect(isSwiped) {
                  if (!isSwiped) return@LaunchedEffect

                  if (swipeToDismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd) {
                     onSwipeStartToEnd.invoke(item)
                  } else {
                     onSwipeEndToStart.invoke(item)
                  }
                  swipeToDismissState.reset()
                  isSwiped = false
               }

               SwipeToDismissBox(
                  state = swipeToDismissState,
                  enableDismissFromStartToEnd = enableSwipeStartToEnd,
                  enableDismissFromEndToStart = enableSwipeEndToStart,
                  backgroundContent = {
                     if (swipeToDismissState.dismissDirection == SwipeToDismissBoxValue.StartToEnd)
                        swipeStartToEndContent()
                     else if (swipeToDismissState.dismissDirection == SwipeToDismissBoxValue.EndToStart)
                        swipeEndToStartContent()
                  }
               ) {

                  content(item)

               }
            }
         }
      }
   }
}