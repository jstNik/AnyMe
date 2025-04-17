package com.example.anyme.domain.mal_dl

import com.google.gson.annotations.SerializedName

data class MyListStatus(
    @SerializedName("is_rewatching")
    var isRewatching: Boolean = false,
    @SerializedName("num_episodes_watched")
    var numEpisodesWatched: Int = 0,
    @SerializedName("score")
    var score: Int = 0,
    @SerializedName("status")
    var status: Status = Status.Undefined,
    @SerializedName("updated_at")
    var updatedAt: String = ""
){

    enum class Status{
        Watching{
            override fun toString() = "watching"
        },
        PlanToWatch{
            override fun toString() = "plan_to_watch"
        },
        OnHold{
            override fun toString() = "on_hold"
        },
        Dropped{
            override fun toString() = "dropped"
        },
        Completed{
            override fun toString() = "completed"
        },
        Undefined{
            override fun toString() = ""
        };

        companion object {
            fun getEnum(value: String): Status = when (value) {
                Watching.toString() -> Watching
                PlanToWatch.toString() -> PlanToWatch
                OnHold.toString() -> OnHold
                Dropped.toString() -> Dropped
                Completed.toString() -> Completed
                else -> Undefined
            }
        }

    }

}