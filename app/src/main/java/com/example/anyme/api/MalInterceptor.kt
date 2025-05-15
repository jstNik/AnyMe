package com.example.anyme.api

import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class MalInterceptor(
    private val tokenManager: MalTokenManager
): Interceptor {


    override fun intercept(chain: Interceptor.Chain): Response {
        var accessToken = tokenManager.getAuthToken()
        var response = chain.proceed(buildRequest(chain, accessToken))
        if(response.code() == 401) {
            accessToken = tokenManager.getRefreshedAuthToken()
            response = chain.proceed(buildRequest(chain, accessToken))
        }
        return response
    }

    private fun buildRequest(chain: Interceptor.Chain, accessToken: String?): Request =
        chain.request()
            .newBuilder()
            .addHeader("Authorization", "Bearer $accessToken")
            .build()

}