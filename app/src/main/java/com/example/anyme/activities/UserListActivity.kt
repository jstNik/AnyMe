package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.ui.composables.SearchBar
import com.example.anyme.ui.composables.AnimatedTabRow
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.viewmodels.UserListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListActivity : AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContent {
         AnyMeTheme(darkTheme = true) {
            ComposeUserListActivity()
         }
      }
   }

}

@Composable
fun ComposeUserListActivity(
   viewModel: UserListViewModel = viewModel()
) {

   Scaffold(
      contentWindowInsets = WindowInsets.safeDrawing,

      modifier = Modifier.fillMaxSize()
   ) { paddingValues ->

      Column(
         modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
      ) {

         AnimatedTabRow(
            tabLabels = MyList.Status.entries.map { it.toString() },
            edgePadding = 0.dp,
            isScrollable = true
         ) {
            MyList.Status.entries.getOrNull(it)?.let { status ->
               viewModel.myListStatus = status
            }
         }

         SearchBar(
            onTextChange = { viewModel.filter = it },
            modifier = Modifier.fillMaxWidth()
         )


         val list = viewModel.list.collectAsLazyPagingItems()
         val lazyColumnState = rememberLazyListState()

         SwipeUpToRefresh(
            lazyColumnState,
            onRefresh = { },
         ) { scrollableState ->

            LazyColumnList(
               scrollableState,
               listSize = { list.itemCount },
               getElement = { list[it] },
               key = {
                  val item = list.peek(it)?.media
                  if (item != null && item.id != 0)
                     item.id
                  else
                     (-it - 1)
               }
            ) { idx, item ->
               item.Compose {

               }
            }
         }
      }
   }
}

@Preview
@Composable
fun ComposeUserListActivityPreview() {
   AnyMeTheme {
      ComposeUserListActivity()
   }
}