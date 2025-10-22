package com.example.anyme.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search

const val Debug = false

val CS @Composable get() = MaterialTheme.colorScheme
val Typo @Composable get() = MaterialTheme.typography
enum class Pages(
   val route: String,
   val label: String,
   val icon: ImageVector,
   val contentDescription: String?,
   ) {
   List("list", "List", Icons.AutoMirrored.Outlined.FormatListBulleted, "My list"),
   Explore("explore", "Explore", Icons.Outlined.Explore, "Explore"),
   Search("search", "Search", Icons.Outlined.Search, "Search"),
   Calendar("calendar", "Calendar", Icons.Outlined.CalendarMonth, "Calendar"),;
   //   Profile("Profile", Icons.Outlined.Person, "Profile")

}

const val Details = "details"

