package com.example.anyme.api

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.runBlocking
import net.openid.appauth.AuthState
import okhttp3.Interceptor
import okhttp3.Response
import java.lang.Thread.sleep
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore

class MalInterceptor(private val context: Context): Interceptor {

    private val authState get() = context
        .getSharedPreferences("AUTH_STATE_PREFERENCE", Context.MODE_PRIVATE)
        .getString("AUTH_STATE", null)
        ?.let { AuthState.jsonDeserialize(it) }

    private val permits = 1
    private var semaphores: ConcurrentHashMap<String, Semaphore> = ConcurrentHashMap()


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val domain = request.url().host()
        val semaphore = getASemaphore(domain)
        val response: Response
        try {
            semaphore.acquire()
            response = chain.proceed(
                request
                    .newBuilder()
                    .addHeader("Authorization", "Bearer ${authState?.accessToken ?: ""}")
                    .build()
            )
            return response
        } finally {
            Thread {
                try { sleep(500) } finally { semaphore.release() }
            }.start()
        }

    }


    private fun getASemaphore(host: String): Semaphore{
        var semaphore = semaphores[host]
        if(semaphore != null)
            return semaphore
        semaphore = Semaphore(permits)
        semaphores[host] = semaphore
        return semaphore
    }

}