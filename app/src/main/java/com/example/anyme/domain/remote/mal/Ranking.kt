package com.example.anyme.domain.remote.mal

import com.google.gson.annotations.SerializedName

data class Ranking(
   @SerializedName("rank")
   var rank: Int = 0
)