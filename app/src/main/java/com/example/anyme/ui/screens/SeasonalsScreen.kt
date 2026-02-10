package com.example.anyme.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anyme.ui.composables.AnimatedTabRow
import com.example.anyme.ui.composables.LazyColumnList
import com.example.anyme.ui.composables.SwipeUpToRefresh
import com.example.anyme.ui.navigation.Screen
import com.example.anyme.ui.theme.cs
import com.example.anyme.ui.theme.typo
import com.example.anyme.utils.Resource
import com.example.anyme.utils.shift
import com.example.anyme.viewmodels.RefreshingBehavior.RefreshingStatus
import com.example.anyme.viewmodels.SeasonalsViewModel
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalTime
import kotlinx.datetime.plus


@Composable
fun SeasonalsScreen(
   viewModel: SeasonalsViewModel = hiltViewModel<SeasonalsViewModel>(),
   onNavigation: (Screen) -> Unit
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
   val labels = weekDays.map {
      it.dayOfWeek.name.lowercase().capitalize(Locale.current) + " ${it.dayOfMonth}"
   }
   val lazyListState = rememberLazyListState()
   val resource by viewModel.seasonalMedia.collectAsStateWithLifecycle()
   var currentTime: LocalTime? = null
   val isRefreshing by viewModel.isRefreshing.collectAsStateWithLifecycle()
   val onlyInMyList by viewModel.onlyInMyList.collectAsStateWithLifecycle()

   Column(
      modifier = Modifier
         .fillMaxSize()
         .padding(horizontal = 8.dp)
   ) {

      when(resource.status){
         Resource.Status.Success -> {

            val seasonals = resource.data!!

            AnimatedTabRow(
               tabLabels = labels,
               isScrollable = true,
               edgePadding = 0.dp,
            ) { idx ->
               weekDays.getOrNull(idx)?.let { weekDays ->
                  viewModel.selectWeekDay(weekDays.dayOfWeek)
                  chosenDate = weekDays
               }
            }

            Row(
               verticalAlignment = Alignment.CenterVertically,
               horizontalArrangement = Arrangement.Start,
               modifier = Modifier.fillMaxWidth()
            ) {
               Checkbox(
                  checked = onlyInMyList,
                  onCheckedChange = { viewModel.onCheckedChanged(it) }
               )
               Text(
                  "Show only anime in your list"
               )
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                  divisor = { idx, render ->
                     Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(
                           start = 4.dp,
                           top = if(idx == 0) 0.dp else 4.dp,
                           end = 4.dp,
                           bottom = 4.dp
                        )
                     ) {

                        val time = render.offsetDateTime!!.dateTime.time
                        val color = cs.tertiary.copy(alpha = 0.5F)

                        if (time != currentTime) {
                           currentTime = time

                           HorizontalDivider(
                              modifier = Modifier.weight(0.2F).padding(end = 8.dp),
                              thickness = 1.dp,
                              color = color
                           )
                           Text(
                              text = time.toString(),
                              style = typo.titleLarge.copy(
                                 fontWeight = FontWeight.Bold,
                                 color = color
                              )
                           )
                           HorizontalDivider(
                              modifier = Modifier.weight(0.8F).padding(start = 8.dp),
                              thickness = 1.dp,
                              color = color
                           )
                        }
                     }
                  }
               ) { _, render ->

                  if (render.offsetDateTime!!.dateTime.date == chosenDate)
                     render.Compose {
                        onNavigation(
                           Screen.Details(render.media.id, render.media.host)
                        )
                     }

               }
            }

         }
         Resource.Status.Loading -> { /* TODO */ }
         Resource.Status.Failure -> { /* TODO */ }
      }
   }
}
