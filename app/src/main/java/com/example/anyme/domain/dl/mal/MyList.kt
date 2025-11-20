package com.example.anyme.domain.dl.mal

import android.os.Parcelable
import com.example.anyme.domain.dl.ListStatus
import com.example.anyme.utils.time.OffsetDateTime
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class MyList(
   @SerializedName("is_rewatching")
   var isRewatching: Boolean = false,
   @SerializedName("num_episodes_watched")
   var numEpisodesWatched: Int = 0,
   @SerializedName("score")
   var score: Int = 0,
   @SerializedName("status")
   var status: Status = Status.Unknown,
   @SerializedName("updated_at")
   var updatedAt: OffsetDateTime? = null
): Parcelable {

   enum class Status: ListStatus {
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
      Unknown{
         override fun toString() = ""
         override fun toText() = toString()
      };

      abstract fun toText(): String

      companion object {
         fun getEnum(value: String?): Status = when (value) {
            Watching.toString() -> Watching
            PlanToWatch.toString() -> PlanToWatch
            OnHold.toString() -> OnHold
            Dropped.toString() -> Dropped
            Completed.toString() -> Completed
            else -> Unknown
         }
      }

   }

}