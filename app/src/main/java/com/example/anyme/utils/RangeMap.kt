package com.example.anyme.utils

open class RangeMap<V>(
   private val map: MutableMap<IntRange, V> = mutableMapOf<IntRange, V>(),
): MutableMap<IntRange, V> by map {

   fun get(key: Int): V? {
      for ((range, value) in map) {
         if (key in range) return value
      }
      return null
   }

}