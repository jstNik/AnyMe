package com.example.anyme.data.evaluators

import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.MalAnime
import kotlin.reflect.KMutableProperty1

interface SourcesMerger<T: Media> {

   fun merge(apiMedia: T, dbMedia: T, properties: Set<KMutableProperty1<MalAnime, out Any?>>): T


}