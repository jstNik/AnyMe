package com.example.anyme.remote.interceptors

import com.example.anyme.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class TmdbInterceptor: Interceptor {

   override fun intercept(chain: Interceptor.Chain): Response {
      val url = chain.request().url
      val newUrl = url.newBuilder()
         .addQueryParameter("api_key", BuildConfig.TMDB_API_KEY)
         .build()
      val newRequest = chain.request().newBuilder().url(newUrl).build()
      return chain.proceed(newRequest)
   }

}