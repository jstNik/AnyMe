package com.example.anyme.ui.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun MyListStatusTabRow(
   tabLabels: List<String>,
   onPageSelected: (newStatus: String) -> Unit
){

   var page by remember { mutableIntStateOf(0) }

   AnimatedTabRow(
      selectedTabIndex = page,
      isScrollable = true,
      edgePadding = 0.dp,
      divider = {},
   ) {
      tabLabels.forEachIndexed { index, tabText ->

         Tab(
            selected = page == index,
            onClick = {
               page = index
               onPageSelected.invoke(tabText)
            },
            text = {
               Text(
                  text = tabText,
                  style = MaterialTheme.typography.bodyMedium,
                  color = MaterialTheme.colorScheme.onBackground
               )
            }
         )
      }
   }
}


@Composable
fun AnimatedTabRow(
   selectedTabIndex: Int,
   isScrollable: Boolean,
   modifier: Modifier = Modifier,
   containerColor: Color = TabRowDefaults.primaryContainerColor,
   contentColor: Color = TabRowDefaults.primaryContentColor,
   edgePadding: Dp = TabRowDefaults.ScrollableTabRowEdgeStartPadding,
   indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->

      val animation by animateDpAsState(
         targetValue = tabPositions[selectedTabIndex].contentWidth,
         label = "",
         animationSpec = tween(500, easing = FastOutSlowInEasing)
      )

      TabRowDefaults.PrimaryIndicator(
         width = animation,
         modifier = Modifier
            .tabIndicatorOffset(tabPositions[selectedTabIndex])
      )

   },
   divider: @Composable () -> Unit = @Composable { HorizontalDivider() },
   tabs: @Composable () -> Unit,
) {

   if(!isScrollable)
      TabRow(
         selectedTabIndex = selectedTabIndex,
         modifier = modifier,
         containerColor = containerColor,
         contentColor = contentColor,
         indicator = indicator,
         divider = divider,
         tabs = tabs
      )
   else
      ScrollableTabRow(
         selectedTabIndex = selectedTabIndex,
         modifier = modifier,
         containerColor = containerColor,
         contentColor = contentColor,
         edgePadding = edgePadding,
         indicator = indicator,
         divider = divider,
         tabs = tabs
      )

}