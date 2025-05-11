package com.example.anyme.utils

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.util.Calendar

fun Calendar.getSeason() = when(get(Calendar.MONTH)){
   in 0..2 -> "winter"
   in 3..5 -> "spring"
   in 6..8 -> "summer"
   in 9..11 -> "fall"
   else -> throw IllegalArgumentException("In a year there are 12 months!")
}

fun <C : Collection<T>, T> C.shift(position: Int): List<T>{
   val shiftedList = mutableListOf<T>()
   forEachIndexed { idx, item ->
      shiftedList.add((idx + position).mod(size), item)
   }
   return shiftedList
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