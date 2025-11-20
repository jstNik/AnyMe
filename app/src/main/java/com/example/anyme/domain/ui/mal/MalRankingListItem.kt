package com.example.anyme.domain.ui.mal

import androidx.compose.runtime.Immutable
import com.example.anyme.data.mappers.LayerMapper
import com.example.anyme.data.visitors.ConverterAcceptor
import com.example.anyme.data.visitors.ConverterVisitor
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.Media
import com.example.anyme.remote.Host

@Immutable
data class MalRankingListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val rank: Int = 0,
   val numListUsers: Int = 0,
   val mean: Double = 0.0,
   override val host: Host = Host.Unknown
) : Media, ConverterAcceptor {

   override fun <T> acceptConverter(
      converterVisitor: ConverterVisitor,
      map: (LayerMapper) -> T
   ): T = converterVisitor.visit(this, map)
}