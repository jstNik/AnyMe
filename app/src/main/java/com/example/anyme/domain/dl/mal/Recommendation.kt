package com.example.anyme.domain.dl.mal


import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.MediaWrapper
import com.google.gson.annotations.SerializedName

data class Recommendation(
    @SerializedName("node")
    var animeNode: MalAnimeNode = MalAnimeNode(),
    @SerializedName("num_recommendations")
    var numRecommendations: Int = 0
): MediaWrapper {

    override val media: MalAnime
        get() = animeNode.mapToMalAnimeDL()

}