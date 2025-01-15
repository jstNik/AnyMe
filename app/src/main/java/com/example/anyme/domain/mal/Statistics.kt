package com.example.anyme.domain.mal


import com.fasterxml.jackson.annotation.JsonProperty

data class Statistics(
    @JsonProperty("num_list_users")
    var numListUsers: Int = 0,
    @JsonProperty("status")
    var status: Status = Status()
)