package com.example.anyme.domain.dl.mal

import com.example.anyme.utils.OffsetDateTime
import com.example.anyme.utils.OffsetWeekTime
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

data class NextEpisode(
    val number: Int = 0,
    val releaseDate: OffsetDateTime? = null
)