package com.example.anyme.utils.time

import android.annotation.SuppressLint
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import com.example.anyme.utils.LocalTimeParceler
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.IllegalTimeZoneException
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.WriteWith
import kotlinx.serialization.Serializable
import java.time.format.DateTimeParseException
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@SuppressLint("ParcelCreator")
@Serializable
class OffsetWeekTime private constructor(
   val weekDay: DayOfWeek,
   val time: LocalTime,
   val offset: Offset
): Parcelable {

   fun toZone(timeZone: TimeZone): OffsetWeekTime {
      val duration = time.toSecondOfDay().seconds - offset.value

      val weekDays = DayOfWeek.entries
      val newWeekDay = if (duration < 0.hours) weekDays[abs(weekDay.ordinal - 1) % weekDays.size]
      else if (duration > 24.hours) weekDays[(weekDay.ordinal + 1) % weekDays.size]
      else weekDay

      val localDateTime =
         Instant.fromEpochSeconds(duration.inWholeSeconds).toLocalDateTime(timeZone)

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

   override fun describeContents(): Int {
      return 0
   }

   override fun writeToParcel(parcel: Parcel, flags: Int) {
      parcel.writeString(weekDay.toString())
      parcel.writeString(time.toString())
      parcel.writeParcelable(offset, flags)
   }

   companion object: Parcelable.Creator<OffsetWeekTime> {


      fun create(weekDay: String, time: String, offset: Offset): OffsetWeekTime? {
         try {
            val dayOfWeek = DayOfWeek.valueOf(weekDay.uppercase())
            val localTime = LocalTime.parse(time)
            return OffsetWeekTime(dayOfWeek, localTime, offset)
         } catch (_: Exception){
            return null
         }
      }

      fun create(weekDay: String, time: String): OffsetWeekTime? {
         try {
            val (time, offset) = Offset.splitTimeWithOffset(time)
            return create(weekDay, time, Offset.create(offset))
         } catch (_: Exception) {
            return null
         }
      }


      fun create(weekDay: String, time: String, timeZone: TimeZone): OffsetWeekTime? {
         try {
            val dayOfWeek = DayOfWeek.valueOf(weekDay.uppercase())
            val localTime = LocalTime.parse(time)
            return create(dayOfWeek, localTime, timeZone)
         } catch (_: Exception) {
            return null
         }
      }

      fun create(weekDay: DayOfWeek, time: LocalTime, timeZone: TimeZone): OffsetWeekTime? {
         try {
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
         } catch (_: Exception) {
            return null
         }
      }

      override fun createFromParcel(parcel: Parcel?): OffsetWeekTime? {
         return try{
            val weekDay = parcel?.readString()!!
            val time = parcel.readString()!!
            val offset = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
               parcel.readParcelable(Offset::class.java.classLoader, Offset::class.java)!!
            } else {
               @Suppress("DEPRECATION")
               parcel.readParcelable(Offset::class.java.classLoader)!!
            }
            create(weekDay, time, offset)
         } catch (e: Exception){
            Log.e("$e", "${e.message}", e)
            null
         }
      }

      override fun newArray(size: Int): Array<out OffsetWeekTime?> {
         return arrayOfNulls(size)
      }

   }

}