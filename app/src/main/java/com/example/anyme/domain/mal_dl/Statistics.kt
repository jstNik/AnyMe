package com.example.anyme.domain.mal_dl


import com.fasterxml.jackson.annotation.JsonProperty

data class Statistics(
    @JsonProperty("num_list_users")
    var numListUsers: Int = 0,
    @JsonProperty("status")
    var status: Status = Status()
)