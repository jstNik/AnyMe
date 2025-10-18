package com.example.anyme.remote.interceptors

import android.util.Log
import com.example.anyme.utils.MutexConcurrentHashMap
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody

class BufferInterceptor(
   private val queue: MutexConcurrentHashMap = MutexConcurrentHashMap()
): Interceptor {

   override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
      val domain = request.url.host
      val mutex = queue[domain]
      try {
         mutex.acquire()
         return chain.proceed(request)
      } catch(e: InterruptedException){
         Log.e("$e", "${e.message}", e)
         return Response.Builder()
            .message("Request cancelled because of an Interrupted Exception")
            .body("$e".toResponseBody(null))
            .build()
      }
      finally {
         Thread {
            try {
               Thread.sleep(500)
            } finally { mutex.release() }
         }.start()
      }
   }
}