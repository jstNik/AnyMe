package com.example.anyme.domain.mal_api

import com.example.anyme.domain.mal_dl.MalAnimeDL
import com.google.gson.annotations.SerializedName

data class Data(
   @SerializedName("node")
   var malAnimeDL: MalAnimeDL = MalAnimeDL()
)
