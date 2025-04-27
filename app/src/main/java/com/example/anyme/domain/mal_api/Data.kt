package com.example.anyme.domain.mal_api

import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.example.anyme.domain.ui.MalRankingListItem
import com.google.gson.annotations.SerializedName

data class Data(
   @SerializedName("node")
   var malAnimeDL: MalAnimeDL = MalAnimeDL(),
   @SerializedName("ranking")
   var ranking: Ranking = Ranking()
){

   fun mapToMalRankingListItem() =
      MalRankingListItem(
         malAnimeDL.id,
         malAnimeDL.title,
         malAnimeDL.mainPicture.medium,
         ranking.rank
      )

}
