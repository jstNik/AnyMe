package com.example.anyme.ui.screens

import android.content.Intent
import android.util.Log
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.ui.composables.AnimatedTabRow
import com.example.anyme.ui.composables.AnyMeScaffold
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SearchBar
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.renders.mal.MalUserListAnimeRender
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.CS
import com.example.anyme.ui.theme.Details
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.ui.theme.Pages
import com.example.anyme.utils.Resource
import com.example.anyme.utils.Result
import com.example.anyme.viewmodels.UserListViewModel
import kotlinx.coroutines.flow.flowOf
import java.nio.file.NotLinkException

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
            onRefresh = { },
         ) { scrollableState ->

            LazyColumnList(
               scrollableState,
               listSize = { list.itemCount },
               getElement = { list[it] },
               modifier = Modifier
                  .background(CS.background)
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
                  navigator.navigate("$Details/${item.media.host}/${item.media.id}")
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