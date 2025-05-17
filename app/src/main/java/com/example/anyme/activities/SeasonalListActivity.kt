package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anyme.ui.composables.AnyMeScaffold
import com.example.anyme.ui.composables.MyListStatusTabRow
import com.example.anyme.ui.composables.RefreshingColumnList
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.utils.shift
import com.example.anyme.viewmodels.SeasonalAnimeListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.LocalDate
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

@AndroidEntryPoint
class SeasonalListActivity: AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContent{
         AnyMeTheme {
            ComposeSeasonalListActivity()
         }
      }
   }

}

@Composable
fun ComposeSeasonalListActivity(viewModel: SeasonalAnimeListViewModel = viewModel()){
   AnyMeScaffold (

      topBar = {

         val weekDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1

         val labels = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            .shift(weekDay)

         MyListStatusTabRow(labels) { }
      },
      content = { paddingValues ->

         val lazyListState = rememberLazyListState()
         val seasonals by viewModel.seasonalAnimes.collectAsStateWithLifecycle()
         val epochDays = Calendar.getInstance().timeInMillis.milliseconds.inWholeDays.toInt()

         RefreshingColumnList(
            lazyColumnState = lazyListState,
            listSize = seasonals.size,
            getElement = { seasonals.getOrNull(it) },
            content = {

               if(it.releaseDateAt(LocalDate.fromEpochDays(epochDays)))
                  it.Render() { }

            },
            key = { seasonals.getOrNull(it)?.id ?: (-it - 1) },
         )

      }
   )
}