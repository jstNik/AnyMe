package com.example.anyme.domain.mal_api

import com.google.gson.annotations.SerializedName

data class Ranking(
   @SerializedName("rank")
   var rank: Int = 0
)