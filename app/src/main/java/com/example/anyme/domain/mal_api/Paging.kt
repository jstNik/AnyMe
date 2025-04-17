package com.example.anyme.domain.mal_api

import com.google.gson.annotations.SerializedName


data class Paging(
    @SerializedName("previous")
    var previous: String = "",
    @SerializedName("next")
    var next: String = ""
)