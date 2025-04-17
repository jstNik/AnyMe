package com.example.anyme.domain.mal_dl


import com.example.anyme.domain.mal_db.MalAnimeDB
import com.google.gson.annotations.SerializedName

data class RelatedAnime(
    @SerializedName("anime")
    var malAnimeApi: MalAnime = MalAnimeDB(),
    @SerializedName("relation_type")
    var relationType: String = "",
    @SerializedName("relation_type_formatted")
    var relationTypeFormatted: String = ""
)