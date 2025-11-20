package com.example.anyme.domain.dl.mal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class Status(
    @SerializedName("completed")
    var completed: String = "",
    @SerializedName("dropped")
    var dropped: String = "",
    @SerializedName("on_hold")
    var onHold: String = "",
    @SerializedName("plan_to_watch")
    var planToWatch: String = "",
    @SerializedName("watching")
    var watching: String = ""
): Parcelable