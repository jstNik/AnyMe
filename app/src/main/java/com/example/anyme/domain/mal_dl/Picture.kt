package com.example.anyme.domain.mal_dl


import com.fasterxml.jackson.annotation.JsonProperty

data class Picture(
    @JsonProperty("large")
    var large: String = "",
    @JsonProperty("medium")
    var medium: String = ""
)