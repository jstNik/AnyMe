package com.example.anyme.data.mappers

import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.ui.MediaDetails
import com.example.anyme.domain.ui.MediaListItem
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.renders.mal.MalAnimeDetailsRender

interface LayerMapper {

//   fun <T: Media> mapToDataBaseEntity(): ConverterAcceptor<T>?
   fun mapDomainToDetails(): MediaDetailsRender
   fun mapDomainToListItem(): MediaListItemRender
   fun mapDomainToSeasonalItem(): MediaListItemRender
   fun mapDomainToGridItem(): MediaListItemRender
   fun  mapDomainToRankingListItem(): MediaListItemRender
   fun mapDetailsToDomain(): ConverterAcceptor
   fun mapGridItemToDomain(): ConverterAcceptor
   fun mapRankingListItemToDomain(): ConverterAcceptor
   fun mapSeasonalItemToDomain(): ConverterAcceptor
   fun mapListItemToDomain(): ConverterAcceptor
   fun mapRelatedItemToDomain(): ConverterAcceptor
   fun mapRecommendationItemToDomain(): ConverterAcceptor

   fun mapDomainToRecommendationItem(): ConverterAcceptor
   fun mapDomainToRelatedItem(): ConverterAcceptor
}