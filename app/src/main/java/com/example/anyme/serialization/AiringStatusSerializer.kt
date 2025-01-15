package com.example.anyme.serialization

import com.example.anyme.domain.mal.Anime
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class AiringStatusSerializer: JsonSerializer<Anime.AiringStatus>() {

    override fun serialize(
        value: Anime.AiringStatus,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) = gen.writeStringField("status", value.toString())
}

class AiringStatusDeserializer: JsonDeserializer<Anime.AiringStatus>(){
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Anime.AiringStatus = Anime.AiringStatus.getEnum(p.getValueAsString(""))
}