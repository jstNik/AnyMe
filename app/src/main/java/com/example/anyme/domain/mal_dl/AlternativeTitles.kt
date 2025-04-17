package com.example.anyme.domain.mal_dl

import com.google.gson.annotations.SerializedName


data class AlternativeTitles(
    @SerializedName("en")
    var en: String = "",
    @SerializedName("ja")
    var ja: String = "",
    @SerializedName("synonyms")
    var synonyms: List<String> = listOf()
)