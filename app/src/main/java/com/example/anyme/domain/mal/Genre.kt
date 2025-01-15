package com.example.anyme.domain.mal


import com.fasterxml.jackson.annotation.JsonProperty

data class Genre(
    @JsonProperty("id")
    var id: Int = 0,
    @JsonProperty("name")
    var name: String = ""
)