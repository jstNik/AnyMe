package com.example.anyme.domain.ui.mal

import androidx.compose.runtime.Immutable
import com.example.anyme.data.visitors.converters.LayerMapper
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.renders.CallbacksBundle
import com.example.anyme.data.visitors.renders.ListItemRenderAcceptor
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.remote.Host
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.getDateOfNext
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar

@Immutable
data class MalSeasonalListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val startDate: OffsetDateTime? = null,
   val endDate: OffsetDateTime? = null,
   val htmlNextEp: Int = 0,
   val htmlReleaseDate: OffsetDateTime? = null,
   override val host: Host = Host.Unknown
): MediaUi, ListItemRenderAcceptor {

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

   override fun <T> acceptConverter(
      converterVisitor: ConverterVisitor,
      map: (LayerMapper) -> T
   ): T = converterVisitor.visit(this, map)

   override fun acceptRender(
      visitor: ListItemRenderVisitor,
      callbacksBundle: CallbacksBundle
   ) = visitor.visit(this, callbacksBundle)

}