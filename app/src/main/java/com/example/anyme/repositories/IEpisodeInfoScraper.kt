package com.example.anyme.repositories

import com.example.anyme.domain.mal_dl.MalAnimeDL

interface IEpisodeInfoScraper {

    suspend fun scrapeEpisodesType(anime: MalAnimeDL): MalAnimeDL

    suspend fun scrapeNextEpInfos(anime: MalAnimeDL): MalAnimeDL
}