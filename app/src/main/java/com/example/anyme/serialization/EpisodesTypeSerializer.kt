package com.example.anyme.serialization

import android.util.Log
import com.example.anyme.domain.mal.Anime
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class EpisodesTypeSerializer: JsonSerializer<Map<IntRange, Anime.EpisodeType>>() {
    override fun serialize(
        value: Map<IntRange, Anime.EpisodeType>,
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

class EpisodeTypeDeserializer: JsonDeserializer<Map<IntRange, Anime.EpisodeType>>(){
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Map<IntRange, Anime.EpisodeType> {
        val episodeType = mutableMapOf<IntRange, Anime.EpisodeType>()

        while(p.nextToken() != JsonToken.END_ARRAY){
            try {
                val firstEp = p.currentName().substringBefore("..").toInt()
                val lastEp = p.currentName().substringAfter("..").toInt()
                val epRange = firstEp..lastEp
                val epType = Anime.EpisodeType.getEnum(p.getValueAsString(""))
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