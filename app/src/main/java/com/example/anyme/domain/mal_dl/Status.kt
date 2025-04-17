package com.example.anyme.domain.mal_dl

import com.google.gson.annotations.SerializedName


data class Status(
    @SerializedName("completed")
    var completed: String = "",
    @SerializedName("dropped")
    var dropped: String = "",
    @SerializedName("on_hold")
    var onHold: String = "",
    @SerializedName("plan_to_watch")
    var planToWatch: String = "",
    @SerializedName("watching")
    var watching: String = ""
)