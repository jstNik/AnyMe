package com.example.anyme.domain.ui

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.anyme.domain.mal_dl.Broadcast
import com.example.anyme.domain.mal_dl.MainPicture
import com.example.anyme.domain.mal_dl.MalAnime
import com.example.anyme.domain.mal_dl.NextEpisode
import com.example.anyme.ui.composables.ListEntry
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import java.util.Calendar
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.milliseconds

data class MalSeasonalListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val broadcast: Broadcast = Broadcast(),
   val startDate: LocalDate = LocalDate(0, 1, 1),
   val endDate: LocalDate = LocalDate(0, 1, 1),
   val nextEp: NextEpisode = NextEpisode()
): ListItem, MalAnime {


   fun isNextEpDuringThisWeek(): Boolean{
      val epochDays = Calendar.getInstance().timeInMillis.milliseconds.inWholeDays.toInt()
      val currentDate = LocalDate.fromEpochDays(epochDays)
      val currentWeek = currentDate..currentDate.plus(DatePeriod(days = 6))

      if(nextEp.releaseDate != LocalDateTime(0, 1, 1, 0, 0, 0, 0))
         return nextEp.releaseDate.date in currentWeek

      if (startDate != LocalDate(0, 1, 1) && endDate != LocalDate(0, 1, 1))
         return startDate in currentWeek || currentDate in startDate..endDate || endDate in currentWeek

      if(endDate == LocalDate(0, 1, 1))
         return startDate in currentWeek || currentDate > startDate
      return false
   }

   fun releaseDateAt(date: LocalDate): Boolean{

      if(nextEp.releaseDate != LocalDateTime(0, 1, 1, 0, 0, 0, 0))
         return nextEp.releaseDate == date

      if (startDate != LocalDate(0, 1, 1) && endDate != LocalDate(0, 1, 1))
         return date in startDate..endDate
                 && broadcast.dayOfTheWeek.equals(date.dayOfWeek.name, true)
      if (endDate == LocalDate(0, 1, 1))
         return date >= startDate && broadcast.dayOfTheWeek.equals(date.dayOfWeek.name, true)
      return false
   }

   @Composable
   override fun Render(
      modifier: Modifier,
      onClick: (ListItem) -> Unit
   ) {

      ListEntry(this, modifier, onClick) {

      }
   }
}