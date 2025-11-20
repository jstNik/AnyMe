package com.example.anyme.data.visitors

import androidx.paging.PagingData
import com.example.anyme.data.repositories.Repository
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.data.repositories.MalRepositoryBundle
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.MediaWrapper
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.local.db.MalDatabase
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DetailsVisitor @Inject constructor(
   private val malRepository: MalRepository
): RepositoryVisitor {

   override suspend fun <T> visit(
      media: MalAnime,
      bundle: suspend (MalRepositoryBundle) -> T
   ): T = bundle(MalRepositoryBundle(media, malRepository))


//      override suspend fun <T> visit(
//      media: TmdbSeries,
//      behavior: suspend (TmdbRepositoryBundle) -> T
//   ): T = coroutineScope {
//      behavior.invoke(TmdbRepository)
//   }

}