package com.example.anyme.domain.remote.mal

import com.example.anyme.domain.dl.mal.Season
import com.google.gson.annotations.SerializedName

data class MalAnimeListGetResponse(
   @SerializedName("data")
   var `data`: List<Data> = listOf(),
   @SerializedName("paging")
   var paging: Paging = Paging(),
   @SerializedName("season")
   var season: Season = Season()
)