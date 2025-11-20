package com.example.anyme.utils

import android.os.Parcel
import android.util.Log
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.utils.time.Date
import com.example.anyme.utils.time.Offset
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.time.OffsetWeekTime
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.parcelize.Parceler
import java.lang.reflect.Type
import kotlin.time.ExperimentalTime

class DateTypeAdapter : JsonSerializer<Date?>, JsonDeserializer<Date?> {
   override fun serialize(
      src: Date?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(src?.toString())

   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): Date? = json?.asString?.let { Date.parse(it) }

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

class EpisodesTypeAdapter: JsonSerializer<MalAnime.EpisodesType>, JsonDeserializer<MalAnime.EpisodesType>{

   override fun serialize(
      src: MalAnime.EpisodesType?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(
      src?.toString() ?:
      MalAnime.EpisodesType.Unknown.toString()
   )


   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): MalAnime.EpisodesType =
      MalAnime.EpisodesType.getEnum(json?.asString ?: "")
}

class AiringStatusAdapter: JsonSerializer<MalAnime.AiringStatus>, JsonDeserializer<MalAnime.AiringStatus>{

   override fun serialize(
      src: MalAnime.AiringStatus?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(
      src?.toString() ?:
      MalAnime.AiringStatus.Unknown.toString()
   )


   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): MalAnime.AiringStatus =
      MalAnime.AiringStatus.getEnum(json?.asString ?: "")
}

class MediaTypeAdapter: JsonSerializer<MalAnime.MediaType>, JsonDeserializer<MalAnime.MediaType>{

   override fun serialize(
      src: MalAnime.MediaType?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(
      src?.toString() ?:
      MalAnime.MediaType.Unknown.toString()
   )


   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): MalAnime.MediaType =
      MalAnime.MediaType.getEnum(json?.asString ?: "")
}

class MyListStatusAdapter: JsonSerializer<MyList.Status>, JsonDeserializer<MyList.Status>{

   override fun serialize(
      src: MyList.Status?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(
      src?.toString() ?:
      MyList.Status.Unknown.toString()
   )


   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): MyList.Status =
      MyList.Status.getEnum(json?.asString ?: "")
}

object LocalTimeParceler: Parceler<LocalTime?> {
   override fun LocalTime?.write(parcel: Parcel, flags: Int) {
      parcel.writeString(this.toString())
   }

   override fun create(parcel: Parcel): LocalTime? {
      return try{
         LocalTime.parse(parcel.readString()!!)
      } catch (e: Exception){
         Log.e("$e", "${e.message}", e)
         null
      }
   }

}