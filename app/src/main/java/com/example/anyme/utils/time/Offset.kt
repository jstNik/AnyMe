package com.example.anyme.utils.time

import kotlin.math.abs
import kotlin.time.Duration

@JvmInline
value class Offset(
   val value: Duration
){

   companion object {
      private val offsetRegex: Regex = Regex("[+-][0-9]{2}:[0-9]{2}(:[0-9]{2}\\.[0-9]*)?$")

      fun splitTimeWithOffset(string: String): Pair<String, String> {
         val match = offsetRegex.find(string)
         return if (match != null) {
            string.replace(match.value, "") to match.value
         } else string to ""
      }

      fun create(string: String): Offset {
         val (_, offset) = splitTimeWithOffset(string)
         return Offset(
            Duration.parse(
               offset
                  .plus(":")
                  .replaceFirst(":", "h")
                  .replaceFirst(":", "m")
                  .replaceFirst(":", "s")
            )
         )
      }

      fun create(duration: Duration) = Offset(duration)

   }

   override fun toString(): String = value.toComponents { h, m, _, _ ->
      val sign = if (value.isNegative()) "-" else "+"
      "%s%02d:%02d".format(sign, abs(h), abs(m))
   }

}