package com.example.anyme.domain.dl.mal

import com.google.gson.annotations.SerializedName


data class Studio(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = ""
)