package com.example.anyme.utils.time

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Long.toLocalDataTime(timeZone: TimeZone = TimeZone.currentSystemDefault()) =
   Instant
   .fromEpochMilliseconds(this)
   .toLocalDateTime(timeZone)