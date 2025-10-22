package com.example.anyme.ui.composables

import androidx.activity.compose.LocalActivity
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.ui.theme.Pages

@Composable
fun NavBar(
   selectedPage: String,
   onClick: (String) -> Unit
) {

   NavigationBar {
      Pages.entries.forEachIndexed { idx, page ->

         NavigationBarItem(
            selected = selectedPage == page.route,
            onClick = {
               onClick(page.route)
            },
            icon = {
               Icon(
                  imageVector = page.icon,
                  contentDescription = page.contentDescription
               )
            },
            label = {
               Text(
                  text = page.label,

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
      NavBar(Pages.List.route){

      }
   }
}