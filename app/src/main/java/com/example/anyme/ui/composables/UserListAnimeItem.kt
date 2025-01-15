package com.example.anyme.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.anyme.domain.mal.Anime
import com.example.anyme.domain.mal.MyListStatus.Status
import com.example.anyme.domain.ui.UserListAnimeUi
import com.example.anyme.ui.theme.AnyMeTheme

@Composable
fun UserListAnimeItem(
    anime: UserListAnimeUi
){

}

@Preview
@Composable
fun PreviewUserListAnimeItem(){

    val anime = UserListAnimeUi(
        title = "Cowboy Bepop",
        numEpisodes = 27,
        myListStatusNumEpisodesWatched = 9,
        myListStatusStatus = Status.Watching,
        episodesType = mapOf(
            0..8 to Anime.EpisodeType.MixedMangaCanon,

        )
    )

    AnyMeTheme {
//        UserListAnimeItem(
//
//        )
    }
}