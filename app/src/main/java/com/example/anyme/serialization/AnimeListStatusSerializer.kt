package com.example.anyme.serialization

import com.example.anyme.domain.mal.MyListStatus
import com.example.anyme.domain.mal.MyListStatus.Status
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class AnimeListStatusSerializer: JsonSerializer<Status>() {

    override fun serialize(
        value: Status,
        gen: JsonGenerator,
        serializers: SerializerProvider
    ) = gen.writeStringField("status", value.toString())

}

class AnimeListStatusDeserializer: JsonDeserializer<Status>(){

    override fun deserialize(
        p: JsonParser,
        ctxt: DeserializationContext
    ): Status = Status.getEnum(p.getValueAsString(""))
}