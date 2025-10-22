package com.example.anyme.ui.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.ui.screens.DetailsScreen
import com.example.anyme.ui.screens.ExploreScreen
import com.example.anyme.ui.screens.SearchScreen
import com.example.anyme.ui.screens.SeasonalScreen
import com.example.anyme.ui.screens.UserListScreen
import com.example.anyme.ui.theme.Details
import com.example.anyme.ui.theme.Pages

@Composable
fun AnyMeScaffold(
   topBar: @Composable () -> Unit = { }
) {
   val navController = rememberNavController()
   val startDestination = Pages.List.route
   var selectedPage by rememberSaveable { mutableStateOf(startDestination) }

   Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
      },
      bottomBar = {
         NavBar(
            selectedPage = selectedPage,
            onClick = { page ->
               selectedPage = page
               navController.navigate(route = page) {
                  popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
               }
            }
         )
      }
   ) { paddingValues ->

      NavHost(navController, startDestination = startDestination) {
         Pages.entries.forEach { pages ->
            composable(route = pages.route) {
               when (pages) {
                  Pages.List -> UserListScreen(navController, paddingValues)
                  Pages.Explore -> ExploreScreen(navController, paddingValues)
                  Pages.Search -> SearchScreen(navController, paddingValues)
                  Pages.Calendar -> SeasonalScreen(navController, paddingValues)
               }
            }
         }
         composable(
            route = "$Details/{host}/{media_id}",
            arguments = listOf(
               navArgument("host") { type = NavType.StringType },
               navArgument("media_id") { type = NavType.IntType }
            )
         ) { backStackEntry ->
            DetailsScreen(navController, paddingValues, hiltViewModel(backStackEntry))
         }
      }
   }
}