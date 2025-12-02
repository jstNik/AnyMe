package com.example.anyme.data.visitors.renders

import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.renders.mal.MalAnimeDetailsRender

class MediaDetailsRenderVisitor(
   private val listItemRenderVisitor: ListItemRenderVisitor
): DetailsRenderVisitor {

   override fun visit(
      media: MalAnimeDetails,
      callbacksBundle: CallbacksBundle
   ) = MalAnimeDetailsRender(
      media,
      media.relatedAnime.map{ it.acceptRender(listItemRenderVisitor, CallbacksBundle()) },
      media.recommendations.map{ it.acceptRender(listItemRenderVisitor, CallbacksBundle()) },
      callbacksBundle
   )
}