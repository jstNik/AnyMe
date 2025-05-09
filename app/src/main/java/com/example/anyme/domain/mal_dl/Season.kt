package com.example.anyme.domain.mal_dl

import com.google.gson.annotations.SerializedName


data class Season(
    @SerializedName("season")
    var season: String = "",
    @SerializedName("year")
    var year: Int = 0
)