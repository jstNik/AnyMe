package com.example.anyme.data.visitors.renders

import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.domain.ui.mal.MalRankingListItem
import com.example.anyme.domain.ui.mal.MalSeasonalListItem
import com.example.anyme.domain.ui.mal.MalUserListItem
import com.example.anyme.ui.renders.mal.MalAnimeSearchFrameRender
import com.example.anyme.ui.renders.mal.MalRankingFrameRender
import com.example.anyme.ui.renders.mal.MalRelatedItemRender
import com.example.anyme.ui.renders.mal.MalSeasonalAnimeRender
import com.example.anyme.ui.renders.mal.MalUserListAnimeRender

interface ListItemRenderVisitor {

   fun visit(media: MalAnimeDetails.MalRelatedAnime, callbacksBundle: CallbacksBundle): MalRelatedItemRender
   fun visit(media: MalListGridItem, callbacksBundle: CallbacksBundle): MalAnimeSearchFrameRender
   fun visit(media: MalRankingListItem, callbacksBundle: CallbacksBundle): MalRankingFrameRender
   fun visit(media: MalSeasonalListItem, callbacksBundle: CallbacksBundle): MalSeasonalAnimeRender
   fun visit(media: MalUserListItem, callbacksBundle: CallbacksBundle): MalUserListAnimeRender

}