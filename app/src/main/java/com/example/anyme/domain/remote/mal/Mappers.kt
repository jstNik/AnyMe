package com.example.anyme.domain.remote.mal

import com.example.anyme.domain.ui.mal.MalRankingListItem

fun Data.mapToMalRankingListItem() =
   MalRankingListItem(
      malAnime.id,
      malAnime.title,
      malAnime.mainPicture,
      ranking.rank,
      malAnime.numListUsers
   )