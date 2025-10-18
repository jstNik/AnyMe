package com.example.anyme.domain.dl.mal


import com.example.anyme.domain.dl.MediaWrapper
import com.google.gson.annotations.SerializedName

data class RelatedAnime(
    @SerializedName("node")
    var animeNode: MalAnimeNode = MalAnimeNode(),
    @SerializedName("relation_type")
    var relationType: String = "",
    @SerializedName("relation_type_formatted")
    var relationTypeFormatted: String = ""
): MediaWrapper {

    override val media: MalAnime
       get () = animeNode.mapToMalAnimeDL()

}