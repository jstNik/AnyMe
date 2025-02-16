package com.example.anyme.domain.mal_dl


import com.example.anyme.domain.mal_db.MalAnimeDB
import com.fasterxml.jackson.annotation.JsonProperty

data class Recommendation(
    @JsonProperty("anime")
    var anime: MalAnime = MalAnimeDB(),
    @JsonProperty("num_recommendations")
    var numRecommendations: Int = 0
)