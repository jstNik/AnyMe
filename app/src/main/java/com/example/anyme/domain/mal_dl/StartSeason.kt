package com.example.anyme.domain.mal_dl

import com.google.gson.annotations.SerializedName


data class StartSeason(
    @SerializedName("season")
    var season: String = "",
    @SerializedName("year")
    var year: Int = 0
)