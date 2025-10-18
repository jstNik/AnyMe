package com.example.anyme.utils

import android.util.Log
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import java.lang.reflect.Type
import kotlin.time.ExperimentalTime

class LocalDateTypeAdapter : JsonSerializer<LocalDate?>, JsonDeserializer<LocalDate?> {
   override fun serialize(
      src: LocalDate?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(src?.toIsoString() ?: "")

   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): LocalDate? = json?.asString?.let {
      LocalDate.parse(it.replace("–", "-"))
   }

}

class OffsetDateTimeAdapter : JsonSerializer<OffsetDateTime?>, JsonDeserializer<OffsetDateTime?> {
   override fun serialize(
      src: OffsetDateTime?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(src?.toString() ?: "")

   @OptIn(ExperimentalTime::class)
   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): OffsetDateTime? = json?.asString?.let { OffsetDateTime.parse(it.replace("–", "-")) }
}

class OffsetWeekTimeAdapter(
   private val timeZone: TimeZone
) : JsonDeserializer<OffsetWeekTime?> {

   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): OffsetWeekTime? {
      return json?.asJsonObject?.let {
         try {
            val (startTime, offset) = Offset.splitTimeWithOffset(it["start_time"].asString)
            if (offset.isBlank())
               OffsetWeekTime.create(
                  it["day_of_the_week"].asString, startTime, timeZone
               )
            else OffsetWeekTime.create(
               it["day_of_the_week"].asString, startTime, Offset.create(offset)
            )
         } catch (e: Exception) {
            Log.e("$e", "${e.message}", e)
            null
         }
      }
   }
}