package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.anyme.domain.mal_dl.MyListStatus
import com.example.anyme.ui.composables.ColumnList
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
            ComposeUserListActivity()
        }
    }

}

@Composable
fun ComposeUserListActivity(
    viewModel: UserAnimeListViewModel = viewModel()
){

    val list = viewModel.list.collectAsLazyPagingItems()
    val lazyColumnState = rememberLazyListState()

    MyListStatusTabRow(
        tabLabels = MyListStatus.Status.entries.map { it.toString() },
    ) { }

    ColumnList(
        list,
        lazyColumnState
    )
}

@Preview
@Composable
fun ComposeUserListActivityPreview(){
    AnyMeTheme {
        ComposeUserListActivity()
    }
}