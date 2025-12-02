package com.example.anyme.data.visitors.repositories

import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.Repository
import com.example.anyme.data.visitors.converters.ConverterAcceptor
import com.example.anyme.domain.dl.ListStatus
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.TypeRanking
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.local.db.OrderOption

interface RepositoryAcceptor<T: Media, R: TypeRanking, L: ListStatus, O: OrderOption>: ConverterAcceptor, Media {

   fun <S> acceptRepository(
      repositoryVisitor: RepositoryVisitor,
      bundle: (Repository<T, R, L, O>, T) -> S
   ): S

}

typealias MalAnimeRepositoryAcceptor = RepositoryAcceptor<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption>