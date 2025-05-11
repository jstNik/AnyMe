package com.example.anyme.utils

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import java.lang.reflect.Type

class LocalDateTypeAdapter: JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
   override fun serialize(
      src: LocalDate?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(src?.toIsoString())

   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): LocalDate = json?.asString.toLocalDate()

}

class LocalDateTimeAdapter: JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime>{
   override fun serialize(
      src: LocalDateTime?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(src?.toIsoString())

   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): LocalDateTime = json?.asString.toLocalDateTime()


}