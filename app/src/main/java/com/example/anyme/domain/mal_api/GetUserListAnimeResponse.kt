package com.example.anyme.domain.mal_api

import com.google.gson.annotations.SerializedName

data class GetUserListAnimeResponse(
    @SerializedName("data")
    var `data`: List<Data> = listOf(),
    @SerializedName("paging")
    var paging: Paging = Paging()
)