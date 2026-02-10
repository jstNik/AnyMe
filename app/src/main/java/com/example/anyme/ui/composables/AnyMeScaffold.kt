package com.example.anyme.ui.composables

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.anyme.ui.navigation.Screen
import com.example.anyme.ui.screens.SeasonalsScreen
import com.example.anyme.ui.screens.DetailsScreen
import com.example.anyme.ui.screens.ExploreScreen
import com.example.anyme.ui.screens.RankingScreen
import com.example.anyme.ui.screens.UserListScreen

const val stackLimit = 8

@Composable
fun AnyMeScaffold(
   topBar: @Composable () -> Unit = { }
) {

   val backStack = rememberNavBackStack(Screen.NavBar.UserList)

   Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
      },
      bottomBar = {

         NavBar(
            selectedScreen = backStack.lastOrNull { it is Screen.NavBar },
            onClick = { screen ->
               val existing = backStack.find { it == screen }
               existing?.let {
                  backStack.remove(it)
               }

               if (backStack.size >= stackLimit) {
                  var nToRemove = backStack.size - stackLimit
                  backStack.removeAll {
                     it is Screen.Details && nToRemove-- > 0
                  }
               }

               backStack.add(existing ?: screen)
            }
         )
      }
   ) { paddingValues ->

      Surface(
         modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
      ) {

         NavDisplay(
            backStack = backStack,
            entryProvider = entryProvider {
               entry<Screen.NavBar.UserList> {
                  UserListScreen { screen ->
                     backStack.add(screen)
                  }
               }
               entry<Screen.NavBar.Ranking> {
                  RankingScreen { screen ->
                     backStack.add(screen)
                  }
               }
               entry<Screen.NavBar.Explore> {
                  ExploreScreen { screen ->
                     backStack.add(screen)
                  }
               }
               entry<Screen.NavBar.Calendar> {
                  SeasonalsScreen { screen ->
                     backStack.add(screen)
                  }
               }
               entry<Screen.Details> { key ->
                  DetailsScreen(key.mediaId, key.mediaHost) { screen ->
                     val existing = backStack.find { it == screen }
                     existing?.let {
                        backStack.remove(it)
                     }

                     if (backStack.size >= stackLimit) {
                        var nToRemove = backStack.size - stackLimit
                        backStack.removeAll {
                           it is Screen.Details && nToRemove-- > 0
                        }
                     }

                     backStack.add(existing ?: screen)
                  }
               }
            },
            entryDecorators = listOf(
               rememberSaveableStateHolderNavEntryDecorator(),
               rememberViewModelStoreNavEntryDecorator()
            ),
            modifier = Modifier
               .fillMaxSize()
         )
      }
   }
}