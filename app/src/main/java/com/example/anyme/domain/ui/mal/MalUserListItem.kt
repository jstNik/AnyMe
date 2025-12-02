package com.example.anyme.domain.ui.mal

import androidx.compose.runtime.Immutable
import com.example.anyme.data.visitors.converters.LayerMapper
import com.example.anyme.data.visitors.converters.ConverterVisitor
import com.example.anyme.data.visitors.renders.CallbacksBundle
import com.example.anyme.data.visitors.renders.ListItemRenderAcceptor
import com.example.anyme.data.visitors.renders.ListItemRenderVisitor
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.domain.ui.MediaUi
import com.example.anyme.remote.Host
import com.example.anyme.utils.RangeMap

@Immutable
data class MalUserListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val numEpisodes: Int = 0,
   val myListStatusNumEpisodesWatched: Int = 0,
   val myListStatus: MyList.Status = MyList.Status.Unknown,
   val status: MalAnime.AiringStatus = MalAnime.AiringStatus.Unknown,
   val episodesType: RangeMap<MalAnime.EpisodesType> = RangeMap(),
   val nextEp: NextEpisode = NextEpisode(),
   val hasNotificationsOn: Boolean = false,
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