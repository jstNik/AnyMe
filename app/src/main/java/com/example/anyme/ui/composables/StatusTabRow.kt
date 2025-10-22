package com.example.anyme.ui.composables

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.anyme.ui.composables.details.TitleSection

@Composable
fun AnimatedTabRow(
   tabLabels: List<String>,
   isScrollable: Boolean,
   modifier: Modifier = Modifier,
   containerColor: Color = TabRowDefaults.primaryContainerColor,
   contentColor: Color = TabRowDefaults.primaryContentColor,
   edgePadding: Dp = TabRowDefaults.ScrollableTabRowEdgeStartPadding,
   divider: @Composable () -> Unit = { },
   onPageSelected: (Int) -> Unit
){

   var page by remember { mutableIntStateOf(0) }
   val indicator: @Composable (tabPositions: List<TabPosition>) -> Unit = @Composable { tabPositions ->

      val animation by animateDpAsState(
         targetValue = tabPositions[page].contentWidth,
         label = "",
         animationSpec = tween(500, easing = FastOutSlowInEasing)
      )

      TabRowDefaults.PrimaryIndicator(
         width = animation,
         modifier = Modifier
            .tabIndicatorOffset(tabPositions[page])
      )

   }
   val tabs = @Composable {
      tabLabels.forEachIndexed { index, tabText ->

         Tab(
            selected = page == index,
            onClick = {
               page = index
               onPageSelected.invoke(page)
            },
            text = {
               Text(
                  text = tabText,
                  style = MaterialTheme.typography.titleMedium,
                  color = MaterialTheme.colorScheme.onBackground,
                  fontWeight = FontWeight.Bold
               )
            }
         )
      }
   }

   if(!isScrollable)
      TabRow(
         selectedTabIndex = page,
         modifier = modifier,
         containerColor = containerColor,
         contentColor = contentColor,
         indicator = indicator,
         divider = divider,
         tabs = tabs
      )
   else
      ScrollableTabRow(
         selectedTabIndex = page,
         modifier = modifier,
         containerColor = containerColor,
         contentColor = contentColor,
         edgePadding = edgePadding,
         indicator = indicator,
         divider = divider,
         tabs = tabs
      )


}