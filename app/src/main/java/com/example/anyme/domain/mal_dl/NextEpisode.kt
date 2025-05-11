package com.example.anyme.domain.mal_dl

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class NextEpisode(
    val number: Int = 0,
    val releaseDate: LocalDateTime = LocalDateTime(0, 1, 1, 0, 0, 0, 0)
)