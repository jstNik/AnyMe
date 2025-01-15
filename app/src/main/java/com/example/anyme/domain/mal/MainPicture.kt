package com.example.anyme.domain.mal


import com.fasterxml.jackson.annotation.JsonProperty

data class MainPicture(
    @JsonProperty("large")
    var large: String = "",
    @JsonProperty("medium")
    var medium: String = ""
)