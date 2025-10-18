package com.example.anyme.domain.remote.mal

import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.google.gson.annotations.SerializedName

data class Data(
   @SerializedName("node")
   var malAnime: MalAnime = MalAnime(),
   @SerializedName("ranking")
   var ranking: Ranking = Ranking()
)
