package com.example.anyme.domain.dl.mal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Parcelize
@Serializable
data class Studio(
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("name")
    var name: String = ""
): Parcelable