package com.example.anyme.domain.ui

import androidx.compose.runtime.Immutable
import com.example.anyme.domain.mal.Anime.AiringStatus
import com.example.anyme.domain.mal.Anime.EpisodeType
import com.example.anyme.domain.mal.MyListStatus.Status

@Immutable
data class UserListAnimeUi(
    val id: Int = 0,
    val title: String = "",
    val mainPictureMedium: String = "",
    val numEpisodes: Int = 0,
    val myListStatusNumEpisodesWatched: Int = 0,
    val myListStatusStatus: Status = Status.Undefined,
    val status: AiringStatus = AiringStatus.Undefined,
    val episodesType: Map<IntRange, EpisodeType> = mapOf(),
    val nextEpIn: Long = 0L,
    val nextEp: Int = 0,
    val hasNotificationsOn: Boolean = false
) {

}