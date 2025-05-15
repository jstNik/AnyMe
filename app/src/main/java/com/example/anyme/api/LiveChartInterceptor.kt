package com.example.anyme.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.net.URL

class LiveChartInterceptor: Interceptor {

   private val url = "https://www.livechart.me/schedule"

   private val cookie = """
      preferences.schedule={"layout":"full","start":"today","sort":"title","sort_dir":"asc",
      "included_marks":{"completed":true,"rewatching":true,"watching":true,"planning":true,
      "considering":true,"paused":false,"dropped":false,"skipping":false,"unmarked":true}}
      """.replace("\n", "").replace(" ", "")

   override fun intercept(chain: Interceptor.Chain): Response {
      val request = chain.request()
      if(request.url().toString() == url) {
         val newRequest = request.newBuilder().addHeader("Cookie", cookie).build()
         return chain.proceed(newRequest)
      }
      return chain.proceed(request)
   }


}