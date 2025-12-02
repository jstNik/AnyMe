package com.example.anyme.domain.remote.mal

import com.example.anyme.data.visitors.converters.LayerMapper
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.Repository
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.repositories.MalAnimeRepositoryAcceptor
import com.example.anyme.data.visitors.repositories.RepositoryVisitor
import com.example.anyme.domain.dl.MalAnimeWrapper
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.remote.Host
import com.google.gson.annotations.SerializedName

data class Data(
   @SerializedName("node")
   override var media: MalAnime = MalAnime(),
   @SerializedName("ranking")
   var ranking: Ranking = Ranking()
): MalAnimeWrapper, MalAnimeRepositoryAcceptor {

   override val id: Int
      get() = media.id
   override val title: String
      get() = media.title
   override val mainPicture: MainPicture
      get() = media.mainPicture
   override val host: Host
      get() = media.host

   override fun <T> acceptConverter(
      converterVisitor: ConverterVisitor,
      map: (LayerMapper) -> T
   ): T = converterVisitor.visit(this, map)

   override fun <S> acceptRepository(
      repositoryVisitor: RepositoryVisitor,
      bundle: (Repository<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption>, MalAnime) -> S
   ): S = media.acceptRepository(repositoryVisitor, bundle)

}
