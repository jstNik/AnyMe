package com.example.anyme.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.material.icons.outlined.Search
import androidx.compose.ui.graphics.Color

const val debug = false

val cs @Composable get() = MaterialTheme.colorScheme
val typo @Composable get() = MaterialTheme.typography
val Color.Companion.Orange: Color
   get() = Color(255, 165, 0)

enum class Pages(
   val route: String,
   val label: String,
   val icon: ImageVector,
   val contentDescription: String?,
   ) {
   List("list", "List", Icons.AutoMirrored.Outlined.FormatListBulleted, "My list"),
   Explore("ranking", "Ranking", Icons.Outlined.Leaderboard, "Ranking"),
   Search("explore", "Explore", Icons.Outlined.Explore, "Explore"),
   Calendar("calendar", "Calendar", Icons.Outlined.CalendarMonth, "Calendar");
   //   Profile("Profile", Icons.Outlined.Person, "Profile")

   companion object {
      val DETAILS = "details"
   }

}

val realGenres = sortedSetOf(1, 2, 5, 46, 28, 4, 8, 10, 26, 47, 14, 7, 22, 24, 36, 30, 37, 41)
val explicitGenres = sortedSetOf(9, 49, 12)
val demographics = sortedSetOf(43, 15, 42, 25, 27)
val themes = sortedSetOf(
   50, 51, 52, 53, 54, 81, 55, 39, 56, 57, 58, 35, 59, 13, 60, 61, 62, 63, 64, 74, 65, 66, 17, 18,
   67, 38, 19, 6, 68, 69, 20, 70, 71, 40, 3, 72, 73, 21, 23, 75, 29, 11, 31, 76, 77, 78, 82, 32, 79,
   83, 80, 48
)


