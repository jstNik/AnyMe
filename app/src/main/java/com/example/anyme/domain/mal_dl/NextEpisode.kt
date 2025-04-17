package com.example.anyme.domain.mal_dl

import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class NextEpisode(
    val nextEp: Int = 0,
    val nextEpIn: Duration = 0.seconds
)