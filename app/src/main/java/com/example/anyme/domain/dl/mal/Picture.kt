package com.example.anyme.domain.dl.mal

import com.google.gson.annotations.SerializedName


data class Picture(
    @SerializedName("large")
    var large: String = "",
    @SerializedName("medium")
    var medium: String = ""
)