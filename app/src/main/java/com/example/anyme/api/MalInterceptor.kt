package com.example.anyme.api

import android.content.Context
import net.openid.appauth.AuthState
import okhttp3.Interceptor
import okhttp3.Response

class MalInterceptor(context: Context): Interceptor {

    private val authState = context
        .getSharedPreferences("AUTH_STATE_PREFERENCE", Context.MODE_PRIVATE)
        .getString("AUTH_STATE", null)
        ?.let { AuthState.jsonDeserialize(it) }

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain
                .request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ${authState?.accessToken ?: ""}")
                .build()
        )
    }
}