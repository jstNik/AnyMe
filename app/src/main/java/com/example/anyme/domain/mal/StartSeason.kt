package com.example.anyme.domain.mal


import com.fasterxml.jackson.annotation.JsonProperty

data class StartSeason(
    @JsonProperty("season")
    var season: String = "",
    @JsonProperty("year")
    var year: Int = 0
)