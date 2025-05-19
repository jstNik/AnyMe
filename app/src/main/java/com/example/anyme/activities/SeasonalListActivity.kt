package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.anyme.ui.composables.AnyMeScaffold
import com.example.anyme.ui.composables.RefreshingColumnList
import com.example.anyme.ui.composables.AnimatedTabRow
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.utils.shift
import com.example.anyme.viewmodels.SeasonalAnimeListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus
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

      topBar = { },
      content = { paddingValues ->

         Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues)
         ) {

            val today by viewModel.today.collectAsStateWithLifecycle()
            val weekDays by remember(today) {
               derivedStateOf {
                  val weekDay = today.dayOfWeek.ordinal
                  DayOfWeek.entries.shift(weekDay).mapIndexed { idx, item ->
                     today.plus(DatePeriod(days = idx))
                  }
               }
            }
            var chosenDate by remember(today){
               mutableStateOf(weekDays[0])
            }
            val labels = weekDays.map{ it.dayOfWeek.name.lowercase() + " ${it.dayOfMonth}" }
            val lazyListState = rememberLazyListState()
            val seasonals by viewModel.seasonalAnimes.collectAsStateWithLifecycle()
            var currentTime: LocalTime? = null

            AnimatedTabRow(
               labels,
               isScrollable = true,
               edgePadding = 0.dp,
            ) { }

            RefreshingColumnList(
               lazyColumnState = lazyListState,
               listSize = { seasonals.size },
               getElement = { seasonals.getOrNull(it) },
               content = { idx, item ->
                  Column(Modifier.fillMaxSize()) {
                     val releaseDate = item.getDateTimeNextEp()
                     if (releaseDate?.date == chosenDate) {

                        if (releaseDate.time != currentTime) {
                           Text(
                              text = releaseDate.time.toString()
                           )
                           currentTime = releaseDate.time
                        }

                        item.Render(Modifier) { }
                     }
                  }
               },
               key = { seasonals.getOrNull(it)?.id ?: (-it - 1) },
            )

         }
      }
   )
}