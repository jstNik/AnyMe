package com.example.anyme.domain.mal_dl

import kotlinx.datetime.LocalDateTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

data class NextEpisode(
    val number: Int = 0,
    val releaseDate: Duration = 0.milliseconds
)