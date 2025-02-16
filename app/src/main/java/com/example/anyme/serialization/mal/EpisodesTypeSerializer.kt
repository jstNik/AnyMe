package com.example.anyme.serialization.mal

import android.util.Log
import com.example.anyme.domain.mal_api.MalAnimeDL
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class EpisodesTypeSerializer: JsonSerializer<Map<IntRange, MalAnimeDL.EpisodeType>>() {
    override fun serialize(
        value: Map<IntRange, MalAnimeDL.EpisodeType>,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) {
        gen.writeStartArray()
        value.forEach { entry ->
            gen.writeStringField("${entry.key.first}..${entry.key.last}", entry.value.toString())
        }
        gen.writeEndArray()
    }
}

class EpisodeTypeDeserializer: JsonDeserializer<Map<IntRange, MalAnimeDL.EpisodeType>>(){
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Map<IntRange, MalAnimeDL.EpisodeType> {
        val episodeType = mutableMapOf<IntRange, MalAnimeDL.EpisodeType>()

        while(p.nextToken() != JsonToken.END_ARRAY){
            try {
                val firstEp = p.currentName().substringBefore("..").toInt()
                val lastEp = p.currentName().substringAfter("..").toInt()
                val epRange = firstEp..lastEp
                val epType = MalAnimeDL.EpisodeType.getEnum(p.getValueAsString(""))
                episodeType[epRange] = epType
            } catch(_: NumberFormatException){
                Log.e("NumberFormatException", "Can not process this value: ${p.currentName()}")
            } catch (ex: Exception){
                Log.e("Unknown Exception", "$ex")
            }
        }
        return episodeType.toMap()
    }
}