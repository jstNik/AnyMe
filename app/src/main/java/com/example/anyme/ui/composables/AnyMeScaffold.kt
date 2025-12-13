package com.example.anyme.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.anyme.ui.screens.DetailsScreen
import com.example.anyme.ui.screens.ExploreScreen
import com.example.anyme.ui.screens.SearchScreen
import com.example.anyme.ui.screens.SeasonalScreen
import com.example.anyme.ui.screens.UserListScreen
import com.example.anyme.ui.theme.LocalNavHostController
import com.example.anyme.ui.theme.Pages
import com.example.anyme.ui.theme.Pages.Companion.DETAILS
import com.example.anyme.viewmodels.DetailsViewModel.Companion.HOST_KEY
import com.example.anyme.viewmodels.DetailsViewModel.Companion.MEDIA_KEY

@Composable
fun AnyMeScaffold(
   topBar: @Composable () -> Unit = { }
) {
   val navigator = LocalNavHostController.current
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
               navigator.navigate(route = page) {
                  popUpTo(navigator.graph.findStartDestination().id) { saveState = true }
                  launchSingleTop = true
                  restoreState = true
               }
            }
         )
      }
   ) { paddingValues ->

      NavHost(navigator, startDestination = startDestination) {
         Pages.entries.forEach { pages ->
            composable(route = pages.route) {
               when (pages) {
                  Pages.List -> UserListScreen(paddingValues)
                  Pages.Explore -> ExploreScreen(paddingValues)
                  Pages.Search -> SearchScreen(paddingValues)
                  Pages.Calendar -> SeasonalScreen(paddingValues)
               }
            }
         }
         composable(
            route = "${DETAILS}/{${HOST_KEY}}/{${MEDIA_KEY}}",
            arguments = listOf(
               navArgument(HOST_KEY) { type = NavType.StringType },
               navArgument(MEDIA_KEY) { type = NavType.IntType }
            )
         ) { backStackEntry ->
            DetailsScreen(paddingValues, hiltViewModel(backStackEntry))
         }
      }
   }
}