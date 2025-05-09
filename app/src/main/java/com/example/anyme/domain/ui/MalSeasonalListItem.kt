package com.example.anyme.domain.ui

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.anyme.domain.mal_dl.Broadcast
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.domain.mal_dl.NextEpisode
import com.example.anyme.ui.composables.ListEntry

data class MalSeasonalListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val broadcast: Broadcast = Broadcast(),
   val nextEp: NextEpisode = NextEpisode()
): ListItem, MalAnime {

   @Composable
   override fun Render(
      modifier: Modifier,
      onClick: (ListItem) -> Unit
   ) {
      ListEntry(this, modifier, onClick) { }
   }
}