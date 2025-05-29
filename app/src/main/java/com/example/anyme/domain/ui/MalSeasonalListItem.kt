package com.example.anyme.domain.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.domain.mal_dl.NextEpisode
import com.example.anyme.ui.composables.ListEntry
import com.example.anyme.utils.getDateOfNext
import com.example.anyme.utils.toLocalDateTime
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

data class MalSeasonalListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   private val startDate: LocalDateTime? = null,
   private val endDate: LocalDateTime? = null,
   private val htmlNextEp: Int = 0,
   private val htmlReleaseDate: LocalDateTime? = null
): ListItem, MalAnime {

   fun getDateTimeNextEp(): LocalDateTime? {

      if(htmlReleaseDate != null)
         return htmlReleaseDate
      if (startDate == null) return null

      val today = Calendar.getInstance().timeInMillis.milliseconds.toLocalDateTime()

      if(today <= startDate)
         return startDate

      val nextWeek = today.getDateOfNext(startDate.dayOfWeek)
      if(endDate == null || nextWeek < endDate)
         return LocalDateTime(nextWeek.date, startDate.time)
      return null
   }

   @Composable
   override fun Render(
      modifier: Modifier,
      onClick: (ListItem) -> Unit
   ) {

      ListEntry(
         imageHeight = 128.dp
      ) { }

   }
}