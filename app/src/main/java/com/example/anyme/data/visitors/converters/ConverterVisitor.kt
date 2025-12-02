package com.example.anyme.data.visitors.converters

import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.Recommendation
import com.example.anyme.domain.dl.mal.RelatedAnime
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalUserListItem

interface ConverterVisitor {

   fun <T> visit(media: MalAnime, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: Data, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: MalAnimeDetails.MalRelatedAnime, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: MalAnimeDetails, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: MalListGridItem, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: MalRankingListItem, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: MalSeasonalListItem, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: MalUserListItem, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: RelatedAnime, map: (MalAnimeLayerMapper) -> T): T
   fun <T> visit(media: Recommendation, map: (MalAnimeLayerMapper) -> T): T
}