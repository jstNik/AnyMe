package com.example.anyme.utils

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.IllegalTimeZoneException
import java.time.format.DateTimeParseException
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class OffsetWeekTime private constructor(
   val weekDay: DayOfWeek,
   val time: LocalTime,
   val offset: Offset
) {

   fun toZone(timeZone: TimeZone): OffsetWeekTime {
      val duration = time.toSecondOfDay().seconds - offset.value

      val weekDays = DayOfWeek.entries
      val newWeekDay = if(duration < 0.hours) weekDays[abs(weekDay.ordinal - 1) % weekDays.size]
      else if (duration > 24.hours) weekDays[(weekDay.ordinal + 1) % weekDays.size]
      else weekDay

      val localDateTime = Instant.fromEpochSeconds(duration.inWholeSeconds).toLocalDateTime(timeZone)

      return OffsetWeekTime(
         newWeekDay,
         localDateTime.time,
         Offset.create(localDateTime.toInstant(TimeZone.UTC).epochSeconds.seconds - duration)
      )
   }

   override fun hashCode(): Int {
      var result = weekDay.hashCode()
      result = 31 * result + time.hashCode()
      result = 31 * result + offset.hashCode()
      return result
   }

   override fun equals(other: Any?): Boolean {
      if(other !is OffsetWeekTime) return false
      val left = toZone(TimeZone.UTC)
      val right = other.toZone(TimeZone.UTC)
      return left.weekDay == right.weekDay &&
         left.time == right.time &&
         left.offset == right.offset
   }

   override fun toString(): String  = "$weekDay at $time$offset"

   fun toText() = toString().lowercase().capitalize(Locale.current).dropLast(6)

   companion object {

      @Throws(IllegalStateException::class, DateTimeParseException::class, IllegalTimeZoneException::class)
      fun create(weekDay: String, time: String, offset: Offset): OffsetWeekTime {
         val dayOfWeek = DayOfWeek.valueOf(weekDay.uppercase())
         val localTime = LocalTime.parse(time)
         return OffsetWeekTime(dayOfWeek, localTime, offset)
      }

      @Throws(IllegalStateException::class, DateTimeParseException::class, IllegalTimeZoneException::class)
      fun create(weekDay: String, time: String, timeZone: TimeZone): OffsetWeekTime {
         val dayOfWeek = DayOfWeek.valueOf(weekDay.uppercase())
         val localTime = LocalTime.parse(time)
         return create(dayOfWeek, localTime, timeZone)
      }

      fun create(weekDay: DayOfWeek, time: LocalTime, timeZone: TimeZone): OffsetWeekTime {
         val zoneLocalDate = Instant
            .fromEpochSeconds(time.toSecondOfDay().toLong())
            .toLocalDateTime(TimeZone.UTC)
         val utcInstant = zoneLocalDate.toInstant(timeZone).epochSeconds.seconds
         val zoneInstant = time.toSecondOfDay().seconds
         return OffsetWeekTime(
            weekDay,
            zoneLocalDate.time,
            Offset.create(zoneInstant - utcInstant)
         )
      }

   }

}