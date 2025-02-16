package com.example.anyme.domain.mal_dl


import com.fasterxml.jackson.annotation.JsonProperty

data class Broadcast(
    @JsonProperty("day_of_the_week")
    var dayOfTheWeek: String = "",
    @JsonProperty("start_time")
    var startTime: String = ""
)