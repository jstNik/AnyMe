package com.example.anyme.data.visitors.converters

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
import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.domain.ui.mal.mapToMalAnime
import com.example.anyme.domain.ui.mal.mapToRecommendations
import com.example.anyme.domain.ui.mal.mapToRelatedAnime

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

   override fun mapDomainToDetails() =
      malAnime!!.mapToMalAnimeDetails()

   override fun mapDomainToListItem() =
      malAnime!!.mapToMalAnimeListItem()

   override fun mapDomainToSeasonalItem() =
      malAnime!!.mapToMalSeasonalListItem()

   override fun mapDomainToGridItem() =
      malAnime!!.mapToMalListGridItem()

   override fun mapDomainToRankingListItem() =
      malAnimeWrapper!!.mapToMalRankingListItem()

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