package com.example.anyme.domain.mal_api

import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.fasterxml.jackson.annotation.JsonProperty

data class UserListAnime(
    @JsonProperty("data")
    var userMalAnimeDLList: List<MalAnimeDL> = listOf(),
    @JsonProperty("paging")
    var paging: Paging = Paging()
)