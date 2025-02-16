package com.example.anyme.domain.mal_dl


import com.example.anyme.serialization.mal.AnimeListStatusDeserializer
import com.example.anyme.serialization.mal.AnimeListStatusSerializer
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class MyListStatus(
    @JsonProperty("is_rewatching")
    var isRewatching: Boolean = false,
    @JsonProperty("num_episodes_watched")
    var numEpisodesWatched: Int = 0,
    @JsonProperty("score")
    var score: Int = 0,
    @JsonProperty("status")
    @JsonSerialize(using = AnimeListStatusSerializer::class)
    @JsonDeserialize(using = AnimeListStatusDeserializer::class)
    var status: Status = Status.Undefined,
    @JsonProperty("updated_at")
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