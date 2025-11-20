package com.example.anyme.domain.dl.mal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class Season(
    @SerializedName("season")
    var season: String = "",
    @SerializedName("year")
    var year: Int = 0
): Parcelable