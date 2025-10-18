package com.example.anyme.domain.dl.mal

import com.google.gson.annotations.SerializedName


data class Season(
    @SerializedName("season")
    var season: String = "",
    @SerializedName("year")
    var year: Int = 0
)