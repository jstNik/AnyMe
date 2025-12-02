package com.example.anyme.data.visitors.renders

import com.example.anyme.ui.renders.MediaDetailsRender

interface DetailsRenderAcceptor {

   fun acceptRender(visitor: DetailsRenderVisitor, callbacksBundle: CallbacksBundle): MediaDetailsRender

}