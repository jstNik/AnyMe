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
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.ui.composables.AnimatedTabRow
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SearchBar
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.cs
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.ui.theme.Pages.Companion.DETAILS
import com.example.anyme.viewmodels.UserListViewModel

@Composable
fun UserListScreen(
   contentPadding: PaddingValues,
   viewModel: UserListViewModel = hiltViewModel<UserListViewModel>()
) {

   val navigator = LocalNavHostController.current
   val list = viewModel.list.collectAsLazyPagingItems()

   if(list.loadState.refresh !is LoadState.Loading || list.itemCount > 0){

      Column(
         modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
      ) {

         val contentPadding = 8.dp

         AnimatedTabRow(
            tabLabels = MyList.Status.entries.map { it.toText() },
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
            isRefreshing = false,
            onRefresh = { },
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
                  navigator.navigate("$DETAILS/${item.media.host}/${item.media.id}")
                  Log.d("OnClick", "${item.media.title} clicked")
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
      UserListScreen(
         PaddingValues()
      )
   }
}