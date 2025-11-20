package com.example.anyme.domain.dl

import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.RepositoryAcceptor
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.local.db.OrderOption

interface MediaWrapper<T: Media, R: TypeRanking, L: ListStatus, O: OrderOption>: ConverterAcceptor {

   val media: RepositoryAcceptor<T, R, L, O>

}

typealias MalAnimeWrapper = MediaWrapper<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption>