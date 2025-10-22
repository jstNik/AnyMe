package com.example.anyme.utils

import com.example.anyme.utils.time.OffsetDateTime
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.ExperimentalTime

fun LocalDate.getSeason() = when (month.number) {
   in 1..3 -> "winter"
   in 4..6 -> "spring"
   in 7..9 -> "summer"
   in 10..12 -> "fall"
   else -> throw IllegalArgumentException("In a year there are 12 months!")
}

@OptIn(ExperimentalTime::class)
fun Duration.toCurrentDateTime() = Instant
   .fromEpochMilliseconds(inWholeMilliseconds)
   .toLocalDateTime(TimeZone.currentSystemDefault())

fun OffsetDateTime.getDateOfNext(dayOfWeek: DayOfWeek): OffsetDateTime? {
   var dateDifference = (dayOfWeek.ordinal - this.dateTime.date.dayOfWeek.ordinal).mod(7)
   if (dateDifference == 0)
      return this
   dateDifference = (this.dateTime.date.dayOfWeek.ordinal + dateDifference) % 7
   val datePeriod = DatePeriod(days = dateDifference)
   return OffsetDateTime.create(
      LocalDateTime(dateTime.date.plus(datePeriod), dateTime.time),
      TimeZone.currentSystemDefault()
   )
}

fun <T> List<T>.shift(position: Int): List<T> {
   if (position == 0)
      return toList()
   return List(size) { idx ->
      get((idx + position).mod(size))
   }
}

fun LocalDate.Companion.parseOrNull(
   string: String?,
   format: DateTimeFormat<LocalDate> = LocalDate.Formats.ISO
): LocalDate? {
   return string?.let {
      try {
         LocalDate.parse(string, format)
      } catch (_: Exception) {
         null
      }
   }
}

fun LocalDate.toIsoString(): String = try {
   LocalDate.Formats.ISO.format(this)
} catch (_: IllegalArgumentException) {
   ""
}

fun LocalDateTime.toIsoString(): String = try {
   LocalDateTime.Formats.ISO.format(this)
} catch (_: IllegalArgumentException) {
   ""
}