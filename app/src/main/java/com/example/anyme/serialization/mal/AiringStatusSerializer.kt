package com.example.anyme.serialization.mal

import com.example.anyme.domain.mal_api.MalAnimeDL
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class AiringStatusSerializer: JsonSerializer<MalAnimeDL.AiringStatus>() {

    override fun serialize(
        value: MalAnimeDL.AiringStatus,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) = gen.writeStringField("status", value.toString())
}

class AiringStatusDeserializer: JsonDeserializer<MalAnimeDL.AiringStatus>(){
    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): MalAnimeDL.AiringStatus = MalAnimeDL.AiringStatus.getEnum(p.getValueAsString(""))
}