package com.example.anyme.ui.composables

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation3.runtime.NavKey
import com.example.anyme.ui.navigation.Screen
import com.example.anyme.ui.theme.AnyMeTheme

@Composable
fun NavBar(
   selectedScreen: NavKey?,
   onClick: (Screen.NavBar) -> Unit
) {

   val screenList = listOf(
      Screen.NavBar.UserList,
      Screen.NavBar.Ranking,
      Screen.NavBar.Explore,
      Screen.NavBar.Calendar
   )

   NavigationBar {
      screenList.forEach { screen ->

         NavigationBarItem(
            selected = selectedScreen == screen,
            onClick = {
               onClick(screen)
            },
            icon = {
               Icon(
                  imageVector = screen.icon,
                  contentDescription = null
               )
            },
            label = {
               // TODO style text
               Text(
                  text = screen.label,
               )
            }
         )
      }

   }

}

@Preview
@Composable
fun PreviewNavBar(){
   AnyMeTheme {
      NavBar(Screen.NavBar.UserList){

      }
   }
}