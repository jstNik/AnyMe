package com.example.anyme.domain.dl.mal

import com.google.gson.annotations.SerializedName


data class MainPicture(
    @SerializedName("large")
    var large: String = "",
    @SerializedName("medium")
    var medium: String = ""
)