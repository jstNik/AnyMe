package com.example.anyme.domain.dl.mal

import com.google.gson.annotations.SerializedName


data class AlternativeTitles(
    @SerializedName("en")
    var en: String = "",
    @SerializedName("ja")
    var ja: String = "",
    @SerializedName("synonyms")
    var synonyms: List<String> = listOf()
)