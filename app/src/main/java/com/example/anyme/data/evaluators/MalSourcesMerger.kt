package com.example.anyme.data.evaluators

import com.example.anyme.domain.dl.mal.MalAnime
import java.lang.IllegalArgumentException
import kotlin.reflect.KMutableProperty1

object MalSourcesMerger: SourcesMerger<MalAnime> {

   override fun merge(
      apiMedia: MalAnime,
      dbMedia: MalAnime,
      properties: Set<KMutableProperty1<MalAnime, out Any?>>
   ): MalAnime {

      if(dbMedia.id == 0) return apiMedia
      if(apiMedia.id == 0) return dbMedia
      if(apiMedia.id != dbMedia.id)
         throw IllegalArgumentException("Can't merge two different animes together")

      properties.forEach skip@ {
         if(it.name == MalAnime::myList.name) return@skip
         it.setter.call(dbMedia, it.getter.call(apiMedia))
      }

      apiMedia.myList.updatedAt?.let {
         if (dbMedia.myList.updatedAt == null || apiMedia.myList.updatedAt!! >= dbMedia.myList.updatedAt!!)
            return dbMedia.apply{ myList = apiMedia.myList.copy() }
      }
      return dbMedia
   }

}