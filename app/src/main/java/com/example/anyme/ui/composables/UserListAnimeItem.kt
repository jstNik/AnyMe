package com.example.anyme.ui.composables

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.anyme.domain.mal_api.MalAnimeDL
import com.example.anyme.domain.mal_dl.MyListStatus.Status
import com.example.anyme.domain.ui.MalListItem
import com.example.anyme.ui.theme.AnyMeTheme

@Composable
fun UserListAnimeItem(
    anime: MalListItem
){

}

@Preview
@Composable
fun PreviewUserListAnimeItem(){

    val malAnimeDL = MalListItem(
        title = "Cowboy Bepop",
        numEpisodes = 27,
        myListStatusNumEpisodesWatched = 9,
        myListStatusStatus = Status.Watching,
        episodesType = mapOf(
            0..8 to MalAnimeDL.EpisodeType.MixedMangaCanon,

        )
    )

    AnyMeTheme {
//        UserListAnimeItem(
//
//        )
    }
}