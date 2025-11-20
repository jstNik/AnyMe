package com.example.anyme.domain.dl.mal


import com.example.anyme.data.mappers.LayerMapper
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.ConverterVisitor
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.MediaWrapper
import com.example.anyme.local.db.MalOrderOption
import com.google.gson.annotations.SerializedName

data class Recommendation(
    @SerializedName("node")
    var animeNode: MalAnimeNode = MalAnimeNode(),
    @SerializedName("num_recommendations")
    var numRecommendations: Int = 0
): MediaWrapper<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption> {

    override val media: MalAnime
        get() = animeNode.mapToMalAnimeDL()

   override fun <T> acceptConverter(
      converterVisitor: ConverterVisitor,
      map: (LayerMapper) -> T
   ): T = converterVisitor.visit(this, map)

}