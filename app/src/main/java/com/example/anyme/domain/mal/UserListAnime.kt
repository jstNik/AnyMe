package com.example.anyme.domain.mal

import com.fasterxml.jackson.annotation.JsonProperty

data class UserListAnime(
    @JsonProperty("data")
    var userAnimeList: List<Anime> = listOf(),
    @JsonProperty("paging")
    var paging: Paging = Paging()
)