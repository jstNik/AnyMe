package com.example.anyme.remote.token_managers

import android.content.Context
import androidx.core.content.edit
import com.example.anyme.remote.interceptors.ExpiredOrInvalidTokenException
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationService
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Semaphore

class MalTokenManager(
   private val context: Context,
   private val fileName: String,
   private val mapFieldName: String
) {

   private val mutex = Semaphore(1, true)
   private val authState = getState()

   fun getAuthToken(): String? {
      if (authState == null)
         throw ExpiredOrInvalidTokenException("Login must be done")
      val authService = AuthorizationService(context)

      mutex.acquire()
      if (authState.needsTokenRefresh) {
         val latch = CountDownLatch(1)
         var error: AuthorizationException? = null
         authState.performActionWithFreshTokens(authService) { refreshedToken, _, ex ->
            if (ex != null) {
               cleanState()
               error = ex
            } else {
               persistState(authState)
            }
            latch.countDown()
         }

         latch.await()

         if(error != null) throw error
      }

      mutex.release()

      return authState.accessToken

   }

   @Synchronized
   private fun getState() = context
      .getSharedPreferences(fileName, Context.MODE_PRIVATE)
      .getString(mapFieldName, null)?.let {
         AuthState.jsonDeserialize(it)
      }

   private fun persistState(authState: AuthState) {
      context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
         .edit { putString(mapFieldName, authState.jsonSerializeString()) }
   }

   private fun cleanState() {
      context.getSharedPreferences(fileName, Context.MODE_PRIVATE)
         .edit { remove(mapFieldName) }
   }

   fun getRefreshedAuthToken(): String?{
      authState?.needsTokenRefresh = true
      return getAuthToken()
   }

}