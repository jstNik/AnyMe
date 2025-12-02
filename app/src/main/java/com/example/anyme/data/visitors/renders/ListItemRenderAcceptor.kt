package com.example.anyme.data.visitors.renders

import com.example.anyme.ui.renders.MediaListItemRender

interface ListItemRenderAcceptor {

   fun acceptRender(visitor: ListItemRenderVisitor, callbacksBundle: CallbacksBundle = CallbacksBundle()): MediaListItemRender

}