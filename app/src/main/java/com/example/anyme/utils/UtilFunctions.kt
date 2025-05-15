package com.example.anyme.utils

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import java.util.Calendar
import kotlin.time.Duration

fun LocalDate.getSeason() = when(monthNumber){
   in 1..3 -> "winter"
   in 4..6 -> "spring"
   in 7..9 -> "summer"
   in 10..12 -> "fall"
   else -> throw IllegalArgumentException("In a year there are 12 months!")
}

fun Duration.toLocalDateTime() = Instant
   .fromEpochMilliseconds(inWholeMilliseconds)
   .toLocalDateTime(TimeZone.currentSystemDefault())

fun LocalDateTime.getDateOfNext(dayOfWeek: DayOfWeek): LocalDateTime {
   var dateDifference = (dayOfWeek.ordinal - this.dayOfWeek.ordinal).mod(7)
   if(dateDifference == 0)
      return this
   dateDifference = (this.dayOfWeek.ordinal + dateDifference) % 7
   val datePeriod = DatePeriod(days = dateDifference)
   return LocalDateTime(date.plus(datePeriod), time)
}

fun <T> List<T>.shift(position: Int): List<T>{
   if(position == 0)
      return toList()
   return List<T>(size){ idx ->
      get((idx + position).mod(size))
   }
}

fun String?.toLocalDate(): LocalDate =
   this?.let{ LocalDate.Formats.ISO.parseOrNull(it) } ?: LocalDate(0, 1, 1)


fun LocalDate.toIsoString(): String = try {
   LocalDate.Formats.ISO.format(this)
} catch (_: IllegalArgumentException) {
   ""
}

fun String?.toLocalDateTime(): LocalDateTime =
   this?.let{ LocalDateTime.Formats.ISO.parseOrNull(it) } ?: LocalDateTime(0, 1, 1, 0, 0, 0, 0)


fun LocalDateTime.toIsoString(): String = try {
   LocalDateTime.Formats.ISO.format(this)
} catch (_: IllegalArgumentException) {
   ""
}