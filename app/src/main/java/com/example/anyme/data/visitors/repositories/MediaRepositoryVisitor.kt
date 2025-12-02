package com.example.anyme.data.visitors.repositories

import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.domain.dl.mal.MalAnime
import javax.inject.Inject

class MediaRepositoryVisitor @Inject constructor(
   private val malRepository: MalRepository
): RepositoryVisitor {

   override fun <T> visit(
      media: MalAnime,
      bundle: (MalRepository, MalAnime) -> T
   ): T = bundle(malRepository, media)

}