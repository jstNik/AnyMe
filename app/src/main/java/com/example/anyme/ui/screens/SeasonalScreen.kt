package com.example.anyme.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anyme.ui.composables.AnimatedTabRow
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.ui.theme.Pages.Companion.DETAILS
import com.example.anyme.utils.Resource
import com.example.anyme.utils.shift
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import com.example.anyme.viewmodels.SeasonalViewModel
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus


@Composable
fun SeasonalScreen(
   contentPadding: PaddingValues,
   viewModel: SeasonalViewModel = hiltViewModel<SeasonalViewModel>()
) {

   val navigator = LocalNavHostController.current

   Column(
      modifier = Modifier
         .fillMaxSize()
         .padding(contentPadding)
   ) {

      val today by viewModel.today.collectAsStateWithLifecycle()
      val weekDays by remember {
         derivedStateOf {
            val weekDay = today.dayOfWeek.ordinal
            DayOfWeek.entries.shift(weekDay).mapIndexed { idx, _ ->
               today.date.plus(DatePeriod(days = idx))
            }
         }
      }
      var chosenDate by remember(today) { mutableStateOf(weekDays[0]) }
      val labels = weekDays.map { it.dayOfWeek.name.lowercase() + " ${it.dayOfMonth}" }
      val lazyListState = rememberLazyListState()
      val resource by viewModel.seasonalMedia.collectAsStateWithLifecycle()
      var currentTime: LocalTime? = null
      val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()

      when(resource.status){
         Resource.Status.Success -> {

            val seasonals = resource.data!!

            AnimatedTabRow(
               tabLabels = labels,
               isScrollable = true,
               edgePadding = 0.dp,
            ) { idx ->
               weekDays.getOrNull(idx)?.let { weekDays ->
                  chosenDate = weekDays
               }
            }

            SwipeUpToRefresh(
               scrollableState = lazyListState,
               isRefreshing = isRefreshing == RefreshingStatus.Refreshing,
               onRefresh = { viewModel.refresh() }
            ) {

               LazyColumnList(
                  lazyColumnState = lazyListState,
                  listSize = { seasonals.size },
                  getElement = { seasonals.getOrNull(it) },
                  key = {
                     val item = seasonals.getOrNull(it)?.media
                     if (item != null && item.id != 0)
                        item.id
                     else
                        (-it - 1)
                  },
                  divisor = { _, render ->
                     val media = render.media
                     media.getDateTimeNextEp()?.let { releaseDate ->

                        val date = releaseDate.dateTime.date
                        val time = releaseDate.dateTime.time

                        if (date == chosenDate && time != currentTime) {
                           Text(
                              text = time.toString()
                           )
                           currentTime = time
                        }
                     }
                  }
               ) { _, render ->

                  val media = render.media

                  media.getDateTimeNextEp()?.let { releaseDate ->
                     if (releaseDate.dateTime.date == chosenDate)
                        render.Compose {
                           navigator.navigate("$DETAILS/${media.host}/${media.id}")
                        }
                  }
               }
            }

         }
         Resource.Status.Loading -> { /* TODO */ }
         Resource.Status.Failure -> { /* TODO */ }
      }
   }
}
