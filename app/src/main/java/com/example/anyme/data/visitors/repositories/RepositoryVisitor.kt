package com.example.anyme.data.visitors.repositories

import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.domain.dl.mal.MalAnime

interface RepositoryVisitor {

   fun <T> visit(media: MalAnime, bundle: (MalRepository, MalAnime) -> T): T

//   suspend fun  visit(media: TmdbSeries, behavior: suspend (TmdbRepositoryBundle) -> T): T

}