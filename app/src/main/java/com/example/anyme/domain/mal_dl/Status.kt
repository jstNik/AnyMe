package com.example.anyme.domain.mal_dl


import com.fasterxml.jackson.annotation.JsonProperty

data class Status(
    @JsonProperty("completed")
    var completed: String = "",
    @JsonProperty("dropped")
    var dropped: String = "",
    @JsonProperty("on_hold")
    var onHold: String = "",
    @JsonProperty("plan_to_watch")
    var planToWatch: String = "",
    @JsonProperty("watching")
    var watching: String = ""
)