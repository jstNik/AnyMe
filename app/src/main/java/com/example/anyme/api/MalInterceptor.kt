package com.example.anyme.api

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.anyme.utils.MutexConcurrentHashMap
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore

class MalInterceptor(private val context: Context): Interceptor {

    private val authState get() = context
        .getSharedPreferences("AUTH_STATE_PREFERENCE", Context.MODE_PRIVATE)
        .getString("AUTH_STATE", null)?.let {
            AuthState.jsonDeserialize(it)
        }

    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(
            chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer ${authState?.accessToken ?: ""}")
                .build()
        )

}