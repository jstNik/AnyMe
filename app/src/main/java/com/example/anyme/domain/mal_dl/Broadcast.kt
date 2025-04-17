package com.example.anyme.domain.mal_dl

import com.google.gson.annotations.SerializedName


data class Broadcast(
    @SerializedName("day_of_the_week")
    var dayOfTheWeek: String = "",
    @SerializedName("start_time")
    var startTime: String = ""
)