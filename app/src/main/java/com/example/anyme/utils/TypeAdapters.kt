package com.example.anyme.utils

import android.os.Parcel
import android.util.Log
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MalAnime.EpisodesType
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.utils.time.Date
import com.example.anyme.utils.time.Offset
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.time.OffsetWeekTime
import com.google.gson.Gson
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.parcelize.Parceler
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.nullable
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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

class KOffsetDateTimeAdapter: KSerializer<OffsetDateTime?>{

   override val descriptor: SerialDescriptor
      get() = PrimitiveSerialDescriptor("OffsetDateTime", PrimitiveKind.STRING).nullable

   @OptIn(ExperimentalSerializationApi::class)
   override fun serialize(
      encoder: Encoder,
      value: OffsetDateTime?
   ) {
      value?.let {
         return encoder.encodeString("${it.toZone(TimeZone.UTC)}")
      }
      encoder.encodeNull()
   }

   @OptIn(ExperimentalSerializationApi::class)
   override fun deserialize(decoder: Decoder): OffsetDateTime? {
      return if(decoder.decodeNotNullMark())
         OffsetDateTime.parse(decoder.decodeString())
      else
         decoder.decodeNull()
   }

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

class RangeMapEpisodesType: JsonSerializer<RangeMap<EpisodesType>>, JsonDeserializer<RangeMap<EpisodesType>>{

   override fun serialize(
      src: RangeMap<EpisodesType>,
      typeOfSrc: Type,
      context: JsonSerializationContext?
   ): JsonElement {
      val json = JsonObject()
      src.forEach { (key, value) ->
         json.addProperty("$key", "$value")
      }
      return json
   }

   override fun deserialize(
      json: JsonElement,
      typeOfT: Type,
      context: JsonDeserializationContext
   ): RangeMap<EpisodesType> {

      val rangeMap = RangeMap<EpisodesType>()

      json.asJsonObject.entrySet().forEach{ (k, v) ->
         val (start, end) = k.split("..")
         val type = EpisodesType.getEnum(v.asString)
         rangeMap[start.toInt()..end.toInt()] = type
      }
      return rangeMap
   }
}

class EpisodesTypeAdapter: JsonSerializer<EpisodesType>, JsonDeserializer<EpisodesType>{

   override fun serialize(
      src: EpisodesType?,
      typeOfSrc: Type?,
      context: JsonSerializationContext?
   ): JsonElement = JsonPrimitive(
      src?.toString() ?:
      EpisodesType.Unknown.toString()
   )


   override fun deserialize(
      json: JsonElement?,
      typeOfT: Type?,
      context: JsonDeserializationContext?
   ): EpisodesType =
      EpisodesType.getEnum(json?.asString ?: "")
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