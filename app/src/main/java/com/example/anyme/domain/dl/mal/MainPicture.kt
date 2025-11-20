package com.example.anyme.domain.dl.mal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MainPicture(
    @SerializedName("large")
    var large: String = "",
    @SerializedName("medium")
    var medium: String = ""
): Parcelable