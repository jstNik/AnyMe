package com.example.anyme.domain.dl.mal

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable


@Serializable
@Parcelize
data class Statistics(
    @SerializedName("num_list_users")
    var numListUsers: Int = 0,
    @SerializedName("status")
    var status: Status = Status()
): Parcelable