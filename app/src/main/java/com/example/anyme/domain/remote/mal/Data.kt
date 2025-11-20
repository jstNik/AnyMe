package com.example.anyme.domain.remote.mal

import com.example.anyme.data.mappers.LayerMapper
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.RepositoryBundle
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.ConverterVisitor
import com.example.anyme.data.visitors.MalAnimeRepositoryAcceptor
import com.example.anyme.data.visitors.RepositoryAcceptor
import com.example.anyme.data.visitors.RepositoryVisitor
import com.example.anyme.domain.dl.MalAnimeWrapper
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.MediaWrapper
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
): MalAnimeWrapper, MalAnimeRepositoryAcceptor, Media {

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

   override suspend fun <S> acceptRepository(
      repositoryVisitor: RepositoryVisitor,
      bundle: suspend (RepositoryBundle<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption>) -> S
   ): S = media.acceptRepository(repositoryVisitor, bundle)

}
