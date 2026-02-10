package com.example.anyme.domain.ui.mal

import androidx.compose.runtime.Immutable
import com.example.anyme.data.visitors.converters.LayerMapper
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.renders.CallbacksBundle
import com.example.anyme.data.visitors.renders.ListItemRenderAcceptor
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.domain.dl.mal.Genre
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.remote.Host
import com.example.anyme.ui.renders.mal.MalSeasonalAnimeRender
import com.example.anyme.ui.renders.mal.MalUserListAnimeRender
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.getDateOfNext
import com.example.anyme.utils.time.OffsetWeekTime
import com.example.anyme.utils.time.toLocalDataTime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import java.util.Calendar

@Immutable
data class MalSeasonalListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val startDate: OffsetDateTime? = null,
   val endDate: OffsetDateTime? = null,
   val broadcast: OffsetWeekTime? = null,
   val htmlNextEp: Int = 0,
   val htmlReleaseDate: OffsetDateTime? = null,
   val mean: Double = 0.0,
   val synopsis: String = "",
   val genres: List<Genre> = emptyList(),
   val listStatus: MyList.Status = MyList.Status.Unknown,
   override val host: Host = Host.Mal
): MediaUi, ListItemRenderAcceptor {

   val dateTimeNextEp: OffsetDateTime?
      get() {

         if (htmlReleaseDate != null)
            return htmlReleaseDate
         if (startDate == null) return null

         val epoch = Calendar.getInstance().timeInMillis

         val today = OffsetDateTime.create(
            epoch.toLocalDataTime(),
            TimeZone.currentSystemDefault()
         )

         if (today == null)
            return null


         if (today <= startDate)
            return startDate

         if (broadcast != null) {
            val nextWeekDate = today.getDateOfNext(broadcast.weekDay)
            val nextWeekDateTime = OffsetDateTime.create(
               LocalDateTime(nextWeekDate, broadcast.time),
               broadcast.offset
            )?.toZone(TimeZone.currentSystemDefault())

            return if (nextWeekDateTime != null && (endDate == null || nextWeekDateTime < endDate))
               nextWeekDateTime else null
         }

         val nextWeekDate = today.getDateOfNext(startDate.dateTime.date.dayOfWeek)
         val nextWeekDateTime = OffsetDateTime.create(
            LocalDateTime(nextWeekDate, startDate.dateTime.time),
            startDate.offset
         )?.toZone(TimeZone.currentSystemDefault())

         return if (nextWeekDateTime != null && (endDate == null || nextWeekDateTime < endDate))
            nextWeekDateTime else null
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