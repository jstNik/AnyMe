package com.example.anyme.domain.dl.mal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class AlternativeTitles(
    @SerializedName("en")
    var en: String = "",
    @SerializedName("ja")
    var ja: String = "",
    @SerializedName("synonyms")
    var synonyms: List<String> = listOf()
): Parcelable