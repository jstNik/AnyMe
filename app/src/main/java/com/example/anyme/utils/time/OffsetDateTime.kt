package com.example.anyme.utils.time

import android.util.Log
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import java.io.Serializable
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class OffsetDateTime private constructor(
   val dateTime: LocalDateTime,
   val offset: Offset
) : Comparable<OffsetDateTime>, Serializable {

   fun toZone(timeZone: TimeZone): OffsetDateTime{
      val utcDuration = dateTime.toInstant(TimeZone.UTC).epochSeconds.seconds - offset.value
      if(timeZone == TimeZone.UTC)
         return OffsetDateTime(
            Instant.fromEpochSeconds(utcDuration.inWholeSeconds).toLocalDateTime(TimeZone.UTC),
            Offset.create(0.minutes)
         )

      val currentLocalDateTime = Instant
         .fromEpochSeconds(utcDuration.inWholeSeconds)
         .toLocalDateTime(timeZone)

      val currentLocalDuration = currentLocalDateTime.toInstant(TimeZone.UTC).epochSeconds.seconds

      return OffsetDateTime(
         currentLocalDateTime,
         Offset.create(currentLocalDuration - utcDuration)
      )
   }

   override fun toString(): String  = "$dateTime$offset"


   override fun compareTo(other: OffsetDateTime): Int {
      return this.toZone(TimeZone.UTC).dateTime.compareTo(other.toZone(TimeZone.UTC).dateTime)
   }

   override fun equals(other: Any?): Boolean {
      if(other == null) return false
      if(other is OffsetDateTime) return compareTo(other) == 0
      if(other is String) return parse(other)?.let{ compareTo(it) == 0 } ?: false
      return false
   }

   override fun hashCode(): Int {
      val result = dateTime.hashCode()
      return 31 * result + offset.hashCode()
   }

   @Suppress("LocalVariableName")
   companion object {

      private val offsetRegex: Regex = Regex("[+-][0-9]{2}:[0-9]{2}(:[0-9]{2}\\.[0-9]*)?$")
//      private val dateTimeRegex: Regex = Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")

      fun create(duration: Duration, timeZone: TimeZone): OffsetDateTime? {
         return create(Instant.fromEpochSeconds(duration.inWholeSeconds), timeZone)
      }

      fun create(instant: Instant, timeZone: TimeZone): OffsetDateTime? {
         return create(instant.toLocalDateTime(timeZone), timeZone)
      }

      fun create(localDateTime: LocalDateTime?, timeZone: TimeZone): OffsetDateTime? {
         return localDateTime?.let {
            val zoneInstant = it.toInstant(timeZone).epochSeconds.seconds
            val utcInstant = it.toInstant(TimeZone.UTC).epochSeconds.seconds
            OffsetDateTime(it, Offset.create(utcInstant - zoneInstant))
         }
      }

      fun parse(string: String): OffsetDateTime? {
         try {
            val (time, _offset) = Offset.splitTimeWithOffset(string)
            if(_offset.isBlank())
               return parse(string, Offset.create(0.minutes))
            return parse(time, Offset.create(_offset))
         } catch (e: IllegalArgumentException){
            Log.e("$e", "${e.message}", e)
            return parse(string, Offset.create(0.minutes))
         } catch (e: Exception) {
            Log.e("$e", "${e.message}", e)
            return null
         }
      }

      fun parse(string: String, offset: Offset): OffsetDateTime? {
         try {
            val localDateTime = LocalDateTime.parse(string, LocalDateTime.Formats.ISO)
            return OffsetDateTime(localDateTime, offset)
         } catch (e: Exception) {
            Log.e("$e", "${e.message}", e)
            return null
         }
      }

   }

}
