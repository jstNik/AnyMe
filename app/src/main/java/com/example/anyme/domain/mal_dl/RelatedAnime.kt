package com.example.anyme.domain.mal_dl


import com.example.anyme.domain.mal_db.MalAnimeDB
import com.fasterxml.jackson.annotation.JsonProperty

data class RelatedAnime(
    @JsonProperty("anime")
    var malAnimeApi: MalAnime = MalAnimeDB(),
    @JsonProperty("relation_type")
    var relationType: String = "",
    @JsonProperty("relation_type_formatted")
    var relationTypeFormatted: String = ""
)