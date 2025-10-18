package com.example.anyme.domain.dl.mal

import com.google.gson.annotations.SerializedName


data class Statistics(
    @SerializedName("num_list_users")
    var numListUsers: Int = 0,
    @SerializedName("status")
    var status: Status = Status()
)