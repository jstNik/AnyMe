package com.example.anyme.domain.mal


import com.fasterxml.jackson.annotation.JsonProperty

data class AlternativeTitles(
    @JsonProperty("en")
    var en: String = "",
    @JsonProperty("ja")
    var ja: String = "",
    @JsonProperty("synonyms")
    var synonyms: List<String> = listOf()
)