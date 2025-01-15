package com.example.anyme.domain.mal

import com.fasterxml.jackson.annotation.JsonProperty

data class Paging(
    @JsonProperty("previous")
    var previous: String = "",
    @JsonProperty("next")
    var next: String = ""
)