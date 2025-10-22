package com.example.anyme.utils.time

import kotlinx.datetime.LocalDate

class Date private constructor(
   val year: Int,
   val month: Int = 0,
   val day: Int = 0
): Comparable<Date> {


   override fun equals(other: Any?): Boolean {
      val right: Date? = if(other is String) parse(other)
      else other as? Date ?: return false
      return year == right?.year && month == right.month && day == right.day
   }

   override fun toString(): String {
      var string = "%04d".format(year)
      if(month != 0){
         string += "-%02d".format(month)
         if(day != 0){
            string += "-%02d".format(day)
         }
      }
      return string
   }

   fun toLocalDate(): LocalDate? = try {
      LocalDate(year, month, day)
   } catch (_: Exception) {
      null
   }

   companion object {
      fun parse(string: String): Date? {
         try {
            val (year, month, day) = string.split("-")
            if(year.length != 4) return null
            val res = Date(
               year.toInt(),
               if(month.isNotEmpty()) month.toInt() else 0,
               if(day.isNotEmpty()) day.toInt() else 0
            )
            LocalDate(
               res.year,
               if(res.month != 0) res.month else 1,
               if(res.day != 0) res.day else 1
            )
            return res
         } catch(_: Exception){
            return null
         }
      }
   }

   override fun hashCode(): Int {
      var result = year
      result = 31 * result + month
      result = 31 * result + day
      return result
   }

   override fun compareTo(other: Date): Int {
      val ly = year
      val ry = other.year
      if(ly != ry) return ly.compareTo(ry)
      val lm = month
      val rm = other.month
      if(lm != rm) return lm.compareTo(rm)
      val ld = day
      val rd = other.day
      return ld.compareTo(rd)
   }

}