package com.example.anyme.data.visitors.converters

import com.example.anyme.data.visitors.repositories.RepositoryAcceptor
import com.example.anyme.data.visitors.renders.DetailsRenderAcceptor
import com.example.anyme.data.visitors.renders.ListItemRenderAcceptor
import com.example.anyme.domain.dl.MediaWrapper

interface LayerMapper {

//   fun <T: Media> mapToDataBaseEntity(): ConverterAcceptor<T>?
   fun  mapDomainToDetails(): DetailsRenderAcceptor
   fun mapDomainToListItem(): ListItemRenderAcceptor
   fun mapDomainToSeasonalItem(): ListItemRenderAcceptor
   fun mapDomainToGridItem(): ListItemRenderAcceptor
   fun mapDomainToRankingListItem(): ListItemRenderAcceptor
   fun mapDetailsToDomain(): RepositoryAcceptor<*, *, *, *>
   fun mapGridItemToDomain(): RepositoryAcceptor<*, *, *, *>
   fun mapRankingListItemToDomain(): RepositoryAcceptor<*, *, *, *>
   fun mapSeasonalItemToDomain(): RepositoryAcceptor<*, *, *, *>
   fun mapListItemToDomain(): RepositoryAcceptor<*, *, *, *>
   fun mapRelatedItemToDomain(): MediaWrapper<*, *, *, *>
   fun mapRecommendationItemToDomain(): MediaWrapper<*, *, *, *>

   fun mapDomainToRecommendationItem(): ListItemRenderAcceptor
   fun mapDomainToRelatedItem(): ListItemRenderAcceptor
}