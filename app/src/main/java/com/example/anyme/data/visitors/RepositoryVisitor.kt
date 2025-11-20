package com.example.anyme.data.visitors

import com.example.anyme.data.repositories.MalRepositoryBundle
import com.example.anyme.data.repositories.Repository
import com.example.anyme.domain.dl.mal.MalAnime

interface RepositoryVisitor {

   suspend fun <T> visit(media: MalAnime, bundle: suspend (MalRepositoryBundle) -> T): T

//   suspend fun  visit(media: TmdbSeries, behavior: suspend (TmdbRepositoryBundle) -> T): T

}