package com.example.anyme.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.ui.composables.AnimatedTabRow
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SearchBar
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.navigation.Screen
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.cs
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.ui.theme.Pages.Companion.DETAILS
import com.example.anyme.viewmodels.RefreshingBehavior
import com.example.anyme.viewmodels.UserListViewModel

@Composable
fun UserListScreen(
   viewModel: UserListViewModel = hiltViewModel<UserListViewModel>(),
   onNavigation: (Screen) -> Unit
) {

   val list = viewModel.list.collectAsLazyPagingItems()
   val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
   val contentPadding = 8.dp

   if(list.loadState.refresh !is LoadState.Loading || list.itemCount > 0){

      Column(
         modifier = Modifier
            .fillMaxSize()
      ) {

         AnimatedTabRow(
            tabLabels = MyList.Status.entries.filter{ it != MyList.Status.Unknown }.map{ it.toText() },
            edgePadding = 0.dp,
            isScrollable = true
         ) {
            MyList.Status.entries.getOrNull(it)?.let { status ->
               viewModel.setMyListStatus(status)
            }
         }

         SearchBar(
            onTextChange = { viewModel.setFilter(it) },
            modifier = Modifier
               .fillMaxWidth()
               .padding(contentPadding, contentPadding, contentPadding)
         )

         val lazyColumnState = rememberLazyListState()

         SwipeUpToRefresh(
            lazyColumnState,
            isRefreshing = isRefreshing == RefreshingBehavior.RefreshingStatus.Refreshing,
            onRefresh = { viewModel.refresh() },
         ) { ->

            LazyColumnList(
               lazyColumnState,
               listSize = { list.itemCount },
               getElement = { list[it] },
               modifier = Modifier
                  .background(cs.background)
                  .padding(contentPadding),
               key = {
                  val item = list.peek(it)?.media
                  if (item != null && item.id != 0)
                     item.id
                  else
                     (-it - 1)
               }
            ) { idx, item ->

               item.Compose {
                  onNavigation(
                     Screen.Details(item.media.id, item.media.host)
                  )
               }

               if (idx != list.itemCount - 1)
                  Spacer(Modifier.height(contentPadding))

            }
         }
      }

   }

}

@Preview
@Composable
fun ComposeUserListActivityPreview() {
   AnyMeTheme {
      UserListScreen {

      }
   }
}