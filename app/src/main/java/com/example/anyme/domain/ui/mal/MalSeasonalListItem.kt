package com.example.anyme.domain.ui.mal

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.CardColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.Media
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.composables.ListEntry
import com.example.anyme.utils.OffsetDateTime
import com.example.anyme.utils.getDateOfNext
import com.example.anyme.utils.toCurrentDateTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar
import kotlin.time.Duration.Companion.milliseconds

data class MalSeasonalListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   private val startDate: OffsetDateTime? = null,
   private val endDate: OffsetDateTime? = null,
   private val htmlNextEp: Int = 0,
   private val htmlReleaseDate: OffsetDateTime? = null
): Media {

   fun getDateTimeNextEp(): OffsetDateTime? {

      if(htmlReleaseDate != null)
         return htmlReleaseDate
      if (startDate == null) return null

      val epoch = Calendar.getInstance().timeInMillis

      val today = OffsetDateTime.create(
         Instant.fromEpochMilliseconds(epoch).toLocalDateTime(TimeZone.currentSystemDefault()),
         TimeZone.currentSystemDefault()
      )

      if(today == null)
         return null


      if(today <= startDate)
         return startDate

      return today.getDateOfNext(startDate.dateTime.date.dayOfWeek)?.let { nextWeek ->
         if (endDate == null || nextWeek < endDate)
            OffsetDateTime.create(
               LocalDateTime(nextWeek.dateTime.date, startDate.dateTime.time),
               TimeZone.currentSystemDefault()
            )
         else null
      }
   }

}