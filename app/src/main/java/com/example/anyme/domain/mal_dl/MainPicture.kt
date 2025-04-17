package com.example.anyme.domain.mal_dl

import com.google.gson.annotations.SerializedName


data class MainPicture(
    @SerializedName("large")
    var large: String = "",
    @SerializedName("medium")
    var medium: String = ""
)