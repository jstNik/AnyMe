package com.example.anyme.ui.composables

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState


@Composable
fun <T: ScrollableState> SwipeUpToRefresh(
   scrollableState: T,
   onRefresh: () -> Unit = { },
   content: @Composable (T) -> Unit
){
   var isRefreshing by remember { mutableStateOf(false) }
   val swipeRefreshState = rememberSwipeRefreshState(isRefreshing)
   val isSwipeEnabled by derivedStateOf {
      !scrollableState.canScrollBackward && !scrollableState.isScrollInProgress
         || swipeRefreshState.isSwipeInProgress
   }


   LaunchedEffect(isRefreshing) {
      if (isRefreshing) {
         onRefresh.invoke()
         isRefreshing = false
      }
   }

   SwipeRefresh(
      state = swipeRefreshState,
      swipeEnabled = isSwipeEnabled,
      onRefresh = {
         isRefreshing = true
      },
      content = { content(scrollableState) }
   )
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
   divisor: @Composable LazyItemScope.(Int, T) -> Unit = { idx, item -> },
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