package com.example.anyme.domain.ui.mal

import androidx.compose.runtime.Immutable
import com.example.anyme.data.visitors.converters.LayerMapper
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.renders.CallbacksBundle
import com.example.anyme.data.visitors.renders.ListItemRenderAcceptor
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.remote.Host
import com.example.anyme.ui.renders.mal.MalRankingFrameRender

@Immutable
data class MalRankingListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val rank: Int = 0,
   val numListUsers: Int = 0,
   val mean: Double = 0.0,
   val listStatus: MyList.Status = MyList.Status.Unknown,
   override val host: Host = Host.Unknown
) : MediaUi, ListItemRenderAcceptor {

   override fun <T> acceptConverter(
      converterVisitor: ConverterVisitor,
      map: (LayerMapper) -> T
   ): T = converterVisitor.visit(this, map)


   override fun acceptRender(
      visitor: ListItemRenderVisitor,
      callbacksBundle: CallbacksBundle
   ) = visitor.visit(this, callbacksBundle)
}