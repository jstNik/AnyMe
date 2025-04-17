package com.example.anyme.domain.mal_dl


import com.example.anyme.domain.mal_db.MalAnimeDB
import com.google.gson.annotations.SerializedName

data class Recommendation(
    @SerializedName("anime")
    var anime: MalAnime = MalAnimeDB(),
    @SerializedName("num_recommendations")
    var numRecommendations: Int = 0
)