package com.example.anyme.remote.interceptors

import android.util.Log
import com.example.anyme.utils.MutexConcurrentHashMap
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.Calendar
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class BufferInterceptor(
   private val queue: MutexConcurrentHashMap = MutexConcurrentHashMap(),
   private val cooldown: Duration = 500.milliseconds
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
            .code(401)
            .body("$e".toResponseBody(null))
            .build()
      }
      finally {
         try {
            Thread.sleep(
               if(domain.contains("livechart"))
               cooldown.inWholeMilliseconds * 1.5.toLong() else cooldown.inWholeMilliseconds
            )
         } finally {
            mutex.release()
         }
      }
   }
}