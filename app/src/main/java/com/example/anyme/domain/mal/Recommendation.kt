package com.example.anyme.domain.mal


import com.fasterxml.jackson.annotation.JsonProperty

data class Recommendation(
    @JsonProperty("anime")
    var anime: AnimeX = AnimeX(),
    @JsonProperty("num_recommendations")
    var numRecommendations: Int = 0
)