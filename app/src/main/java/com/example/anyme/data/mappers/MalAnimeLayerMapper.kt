package com.example.anyme.data.mappers

import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.Recommendation
import com.example.anyme.domain.dl.mal.RelatedAnime
import com.example.anyme.domain.dl.mal.mapToMalAnimeDetails
import com.example.anyme.domain.dl.mal.mapToMalAnimeListItem
import com.example.anyme.domain.dl.mal.mapToMalListGridItem
import com.example.anyme.domain.dl.mal.mapToMalRelatedAnime
import com.example.anyme.domain.dl.mal.mapToMalSeasonalListItem
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.domain.remote.mal.mapToMalRankingListItem
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.domain.ui.mal.mapToMalAnime
import com.example.anyme.domain.ui.mal.mapToRecommendations
import com.example.anyme.domain.ui.mal.mapToRelatedAnime
import com.example.anyme.ui.renders.MediaDetailsRender
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.renders.mal.MalAnimeDetailsRender
import com.example.anyme.ui.renders.mal.MalAnimeSearchFrameRender
import com.example.anyme.ui.renders.mal.MalRankingFrameRender
import com.example.anyme.ui.renders.mal.MalRelatedItemRender
import com.example.anyme.ui.renders.mal.MalSeasonalAnimeRender
import com.example.anyme.ui.renders.mal.MalUserListAnimeRender

class MalAnimeLayerMapper(
   private val malAnime: MalAnime? = null,
   private val malAnimeWrapper: Data? = null,
   private val malAnimeDetails: MalAnimeDetails? = null,
   private val malListGridItem: MalListGridItem? = null,
   private val malRankingListItem: MalRankingListItem? = null,
   private val malSeasonalListItem: MalSeasonalListItem? = null,
   private val malUserListItem: MalUserListItem? = null,
   private val malRelatedItem: MalAnimeDetails.MalRelatedAnime? = null,
   private val malRecommendationItem: MalAnimeDetails.MalRelatedAnime? = null,
   private val malRecommendation: Recommendation? = null,
   private val malRelatedAnime: RelatedAnime? = null
): LayerMapper {

   override fun mapDomainToDetails(): MediaDetailsRender {

      val malAnimeDetails = malAnime!!.mapToMalAnimeDetails()

      return MalAnimeDetailsRender(
         malAnimeDetails,
         malAnimeDetails.relatedAnime.map { MalRelatedItemRender(it) },
         malAnimeDetails.recommendations.map { MalRelatedItemRender(it) }
      )
   }

   override fun mapDomainToListItem(): MediaListItemRender =
      MalUserListAnimeRender(malAnime!!.mapToMalAnimeListItem())

   override fun mapDomainToSeasonalItem(): MediaListItemRender =
      MalSeasonalAnimeRender(malAnime!!.mapToMalSeasonalListItem())

   override fun mapDomainToGridItem(): MediaListItemRender =
      MalAnimeSearchFrameRender(malAnime!!.mapToMalListGridItem())

   override fun mapDomainToRankingListItem(): MediaListItemRender =
      MalRankingFrameRender(malAnimeWrapper!!.mapToMalRankingListItem())

   override fun mapDetailsToDomain(): MalAnime =
      malAnimeDetails!!.mapToMalAnime()

   override fun mapGridItemToDomain(): MalAnime =
      malListGridItem!!.mapToMalAnime()

   override fun mapRankingListItemToDomain() =
      malRankingListItem!!.mapToMalAnime()

   override fun mapSeasonalItemToDomain() =
      malSeasonalListItem!!.mapToMalAnime()

   override fun mapListItemToDomain() =
      malUserListItem!!.mapToMalAnime()

   override fun mapRelatedItemToDomain() =
      malRelatedItem!!.mapToRelatedAnime()

   override fun mapRecommendationItemToDomain() =
      malRecommendationItem!!.mapToRecommendations()

   override fun mapDomainToRecommendationItem() =
      malRecommendation!!.mapToMalRelatedAnime()

   override fun mapDomainToRelatedItem() =
      malRelatedAnime!!.mapToMalRelatedAnime()

}