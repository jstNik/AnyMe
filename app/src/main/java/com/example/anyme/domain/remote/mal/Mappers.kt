package com.example.anyme.domain.remote.mal

import com.example.anyme.domain.ui.mal.MalRankingListItem

fun Data.mapToMalRankingListItem() =
   MalRankingListItem(
      id = media.id,
      title = media.title,
      mainPicture = media.mainPicture,
      rank = ranking.rank,
      numListUsers = media.numListUsers,
      host = media.host
   )