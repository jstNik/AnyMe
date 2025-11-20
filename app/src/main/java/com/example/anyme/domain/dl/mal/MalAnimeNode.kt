package com.example.anyme.domain.dl.mal

import com.google.gson.annotations.SerializedName

data class MalAnimeNode(
   @SerializedName("id")
   var id: Int = 0,
   @SerializedName("main_picture")
   var mainPicture: MainPicture = MainPicture(),
   @SerializedName("title")
   var title: String = "",
){

   val malAnime: MalAnime
      get() = MalAnime(id = id, title = title, mainPicture = mainPicture)

}
