package com.example.anyme.utils

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore

class MutexConcurrentHashMap(
): ConcurrentHashMap<String, Semaphore>() {

   override fun get(key: String): Semaphore {
      var mutex = super.get(key)
      if(mutex == null){
         mutex = Semaphore(1)
         this[key] = mutex
      }
      return mutex
   }

}