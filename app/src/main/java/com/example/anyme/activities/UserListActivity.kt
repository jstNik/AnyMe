package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.domain.mal_dl.MyListStatus
import com.example.anyme.ui.composables.RefreshingColumnList
import com.example.anyme.ui.composables.MyListStatusTabRow
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.viewmodels.UserAnimeListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserListActivity: AppCompatActivity() {

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
   viewModel: UserAnimeListViewModel = viewModel()
){

   val list = viewModel.list.collectAsLazyPagingItems()
   val lazyColumnState = rememberLazyListState()

   Scaffold (
      contentWindowInsets = WindowInsets.safeDrawing,

      topBar = {
         MyListStatusTabRow(
            tabLabels = MyListStatus.Status.entries.map { it.toString() },
         ) { }
      },
      modifier = Modifier.fillMaxSize()
   ) { paddingValues ->
      RefreshingColumnList(
         list,
         lazyColumnState,
         key = { list[it]?.id ?: (-it - 1) },
         contentPadding = paddingValues
      )
   }
}

@Preview
@Composable
fun ComposeUserListActivityPreview(){
   AnyMeTheme {
      ComposeUserListActivity()
   }
}