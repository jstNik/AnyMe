package com.example.anyme.domain.mal


import com.fasterxml.jackson.annotation.JsonProperty

data class RelatedAnime(
    @JsonProperty("anime")
    var anime: Anime = Anime(),
    @JsonProperty("relation_type")
    var relationType: String = "",
    @JsonProperty("relation_type_formatted")
    var relationTypeFormatted: String = ""
)