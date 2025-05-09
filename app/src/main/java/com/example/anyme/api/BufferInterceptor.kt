package com.example.anyme.api

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.content.MediaType
import com.example.anyme.utils.MutexConcurrentHashMap
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import java.lang.Thread.sleep
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class BufferInterceptor(
   private val queue: MutexConcurrentHashMap = MutexConcurrentHashMap()
): Interceptor {

   override fun intercept(chain: Interceptor.Chain): Response {
      var request = chain.request()
      val domain = request.url().host()
      val mutex = queue[domain]
      try {
         mutex.acquire()
         return chain.proceed(request)
      } catch(e: InterruptedException){
         Log.e("$e", "${e.message}", e)
         return Response.Builder()
            .message("Request cancelled because of an Interrupted Exception")
            .body(ResponseBody.create(null, "$e"))
            .build()
      }
      finally {
         Thread {
            try { sleep(500) } finally { mutex.release() }
         }.start()
      }
   }
}