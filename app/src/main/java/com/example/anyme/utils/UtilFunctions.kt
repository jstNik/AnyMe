package com.example.anyme.utils

import java.time.Month
import java.util.Calendar

fun Calendar.getSeason() = when(Calendar.getInstance().get(Calendar.MONTH)){
   in 0..2 -> "winter"
   in 3..5 -> "spring"
   in 6..8 -> "summer"
   in 9..11 -> "fall"
   else -> throw IllegalArgumentException("In a year there are 12 months!")
}

fun <C : Collection<T>, T> C.shift(position: Int): List<T>{
   val shiftedList = mutableListOf<T>()
   forEachIndexed { idx, item ->
      shiftedList.add((idx + position).mod(size), item)
   }
   return shiftedList
}