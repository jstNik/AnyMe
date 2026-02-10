package com.example.anyme.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.PullToRefreshState
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun <T: ScrollableState> SwipeUpToRefresh(
   scrollableState: T,
   isRefreshing: Boolean,
   modifier: Modifier = Modifier,
   contentAlignment: Alignment = Alignment.TopStart,
   onRefresh: () -> Unit = { },
   pullToRefreshState: PullToRefreshState = rememberPullToRefreshState(),
   enabled: Boolean = true,
   indicator: @Composable BoxScope.() -> Unit = {
      Indicator(
         state = pullToRefreshState,
         isRefreshing = isRefreshing,
         modifier = Modifier.align(Alignment.TopCenter)
      )
   },
   content: @Composable ColumnScope.() -> Unit
){

   val isPullToRefreshedEnabled by remember {
      derivedStateOf {
         pullToRefreshState.distanceFraction > 0F ||
                 !scrollableState.isScrollInProgress && !scrollableState.canScrollBackward
      }
   }

   Box(
      contentAlignment = contentAlignment,
      modifier = Modifier.pullToRefresh(
         isRefreshing = isRefreshing,
         state = pullToRefreshState,
         enabled = enabled && isPullToRefreshedEnabled,
         onRefresh = onRefresh
      ).then(modifier)
   ){
      Column {
         content()
      }
      indicator()
   }
}

@Composable
fun <T> LazyColumnList(
   lazyColumnState: LazyListState,
   listSize: () -> Int,
   getElement: (idx: Int) -> T?,
   modifier: Modifier = Modifier,
   key: ((Int) -> Any)? = null,
   enableSwipeStartToEnd: Boolean = false,
   enableSwipeEndToStart: Boolean = false,
   swipeStartToEndContent: @Composable RowScope.() -> Unit = { },
   swipeEndToStartContent: @Composable RowScope.() -> Unit = { },
   contentPadding: PaddingValues = PaddingValues(),
   onSwipeStartToEnd: (listItem: T) -> Unit = { },
   onSwipeEndToStart: (listItem: T) -> Unit = { },
   divisor: @Composable LazyItemScope.(Int, T) -> Unit = { _, _ -> },
   content: @Composable LazyItemScope.(Int, T) -> Unit,
) {
   LazyColumn(
      state = lazyColumnState,
      contentPadding = contentPadding,
      modifier = modifier
   ) {

      items(
         listSize(),
         key
      ) {

         getElement(it)?.let { item ->

            divisor(it, item)

            if (enableSwipeEndToStart || enableSwipeStartToEnd) {
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
                  content(it, item)
               }
            } else
               content(it, item)
         }
      }
   }
}