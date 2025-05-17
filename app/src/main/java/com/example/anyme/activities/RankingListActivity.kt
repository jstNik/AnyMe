package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anyme.repositories.MalRepository
import com.example.anyme.repositories.MalRepository.RankingListType
import com.example.anyme.ui.composables.NestedLazyRows
import com.example.anyme.ui.composables.RefreshingColumnList
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.viewmodels.RankingListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.LocalDate

@AndroidEntryPoint
class RankingListActivity: AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContent{
         AnyMeTheme(darkTheme = true) {
            ComposeRankingLists()
         }
      }

   }


}

@Composable
fun ComposeRankingLists(viewModel: RankingListViewModel = viewModel()){
   Scaffold(
      topBar = {

      },
      bottomBar = {

      }
   ){ contentPadding ->

      val ranks = RankingListType.entries.map { it.toString() }
      val lazyColumState = rememberLazyListState()

//      RefreshingColumnList(
//         lazyColumnState = lazyColumState,
//         listSize = viewModel.rankingLists.size,
//         getElement = { viewModel.rankingLists.getOrNull(it)?.collectAsStateWithLifecycle() },
//         content = {
//
//            if(it.releaseDateAt(LocalDate.fromEpochDays(epochDays)))
//               it.Render() { }
//
//         },
//         key = { seasonals.getOrNull(it)?.id ?: (-it - 1) },
//      )

      NestedLazyRows(
         ranks,
         viewModel.rankingLists,
         listOf(),
         contentPadding = contentPadding
      )

   }
}