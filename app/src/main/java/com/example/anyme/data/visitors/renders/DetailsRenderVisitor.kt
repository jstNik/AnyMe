package com.example.anyme.data.visitors.renders

import com.example.anyme.domain.ui.mal.MalAnimeDetails
import com.example.anyme.ui.renders.mal.MalAnimeDetailsRender

interface DetailsRenderVisitor {

   fun visit(media: MalAnimeDetails, callbacksBundle: CallbacksBundle): MalAnimeDetailsRender

}