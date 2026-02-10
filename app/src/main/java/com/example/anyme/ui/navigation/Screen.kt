package com.example.anyme.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Leaderboard
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation3.runtime.NavKey
import com.example.anyme.remote.Host
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen: NavKey {

   @Serializable
   sealed class NavBar(): Screen {
      abstract val icon: ImageVector
      abstract val label: String

      @Serializable
      data object UserList: NavBar() {
         override val icon: ImageVector
            get() = Icons.AutoMirrored.Outlined.FormatListBulleted
         override val label: String
            get() = "List"
      }

      @Serializable
      data object Ranking: NavBar() {
         override val icon: ImageVector
            get() = Icons.Outlined.Leaderboard
         override val label: String
            get() = "Ranking"
      }

      @Serializable
      data object Explore: NavBar(){
         override val icon: ImageVector
            get() = Icons.Outlined.Explore
         override val label: String
            get() = "Explore"
      }

      @Serializable
      data object Calendar: NavBar(){
         override val icon: ImageVector
            get() = Icons.Outlined.CalendarMonth
         override val label: String
            get() = "Calendar"
      }
   }
   @Serializable
   data class Details(val mediaId: Int, val mediaHost: Host): Screen
}