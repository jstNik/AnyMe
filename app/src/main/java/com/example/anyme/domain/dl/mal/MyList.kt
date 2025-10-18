package com.example.anyme.domain.dl.mal

import com.example.anyme.utils.OffsetDateTime
import com.google.gson.annotations.SerializedName

data class MyList(
    @SerializedName("is_rewatching")
    var isRewatching: Boolean = false,
    @SerializedName("num_episodes_watched")
    var numEpisodesWatched: Int = 0,
    @SerializedName("score")
    var score: Int = 0,
    @SerializedName("status")
    var status: Status = Status.Undefined,
    @SerializedName("updated_at")
    var updatedAt: OffsetDateTime? = null
){

    enum class Status{
        Watching{
            override fun toString() = "watching"
            override fun toText() = "Watching"
        },
        PlanToWatch{
            override fun toString() = "plan_to_watch"
            override fun toText() = "Plan to watch"
        },
        OnHold{
            override fun toString() = "on_hold"
            override fun toText() = "On Hold"
        },
        Dropped{
            override fun toString() = "dropped"
            override fun toText() = "Dropped"
        },
        Completed{
            override fun toString() = "completed"
            override fun toText() = "Completed"
        },
        Undefined{
            override fun toString() = ""
            override fun toText() = toString()
        };

        abstract fun toText(): String

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