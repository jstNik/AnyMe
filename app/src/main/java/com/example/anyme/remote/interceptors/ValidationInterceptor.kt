package com.example.anyme.remote.interceptors

import android.util.Log
import com.example.anyme.data.repositories.ApiCallNotSuccessfulException
import okhttp3.Interceptor
import okhttp3.Response

class ValidationInterceptor(): Interceptor {


   override fun intercept(chain: Interceptor.Chain): Response {
      val response = chain.proceed(chain.request())
      if (!response.isSuccessful || response.body == null) {
         val unsuccessful = ApiCallNotSuccessfulException(response)
         Log.e("$unsuccessful", unsuccessful.message, unsuccessful)
         throw unsuccessful
      }
      return response
   }
}