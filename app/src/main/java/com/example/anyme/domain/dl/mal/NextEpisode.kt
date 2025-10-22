package com.example.anyme.domain.dl.mal

import com.example.anyme.utils.time.OffsetDateTime

data class NextEpisode(
    val number: Int = 0,
    val releaseDate: OffsetDateTime? = null
)