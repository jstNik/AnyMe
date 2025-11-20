package com.example.anyme.data.visitors

import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.Repository
import com.example.anyme.data.repositories.RepositoryBundle
import com.example.anyme.domain.dl.ListStatus
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.TypeRanking
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.local.db.OrderOption

interface RepositoryAcceptor<T: Media, R: TypeRanking, L: ListStatus, O: OrderOption>: ConverterAcceptor {

   suspend fun <S> acceptRepository(
      repositoryVisitor: RepositoryVisitor,
      bundle: suspend (RepositoryBundle<T, R, L, O>) -> S
   ): S

}

typealias MalAnimeRepositoryAcceptor = RepositoryAcceptor<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption>