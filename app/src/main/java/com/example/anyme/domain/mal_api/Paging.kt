package com.example.anyme.domain.mal_api

import com.fasterxml.jackson.annotation.JsonProperty

data class Paging(
    @JsonProperty("previous")
    var previous: String = "",
    @JsonProperty("next")
    var next: String = ""
)