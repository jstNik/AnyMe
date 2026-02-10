package com.example.anyme.utils

open class RangeMap<V>(
   private val map: MutableMap<IntRange, V> = mutableMapOf(),
): MutableMap<IntRange, V> by map {

   fun get(key: Int): V? {
      for ((range, value) in map) {
         if (key in range) return value
      }
      return null
   }

   fun getEntry(key: Int): Map.Entry<IntRange, V>? {
      for (entry in map) {
         if (key in entry.key) return entry
      }
      return null
   }

   override fun toString(): String = map.toString()


}