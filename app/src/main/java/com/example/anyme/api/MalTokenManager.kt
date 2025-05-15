package com.example.anyme.api

import android.content.Context
import android.content.Context.MODE_PRIVATE
import net.openid.appauth.AuthState
import androidx.core.content.edit
import net.openid.appauth.AuthorizationService

class MalTokenManager(
   private val context: Context
) {

   private val lock = Object()
   private val authState = getState()

   @Volatile
   private var refreshing: Boolean = false

   fun getAuthToken(): String? {
      if (authState == null)
         throw ExpiredOrInvalidTokenException("Login must be done")
      val authService = AuthorizationService(context)
      return synchronized(lock) {

         while (refreshing) lock.wait()
         if (authState.needsTokenRefresh) {
            refreshing = true
            authState.performActionWithFreshTokens(authService) { refreshedToken, _, ex ->

               if (ex != null) {
                  cleanState()
                  throw ex
               } else {
                  persistState(authState)
               }
               refreshing = false
               lock.notifyAll()
            }
         }

         authState.accessToken
      }
   }

   @Synchronized
   private fun getState() = context
      .getSharedPreferences("AUTH_STATE_PREFERENCE", MODE_PRIVATE)
      .getString("AUTH_STATE", null)?.let {
         AuthState.jsonDeserialize(it)
      }

   private fun persistState(authState: AuthState) {
      context.getSharedPreferences("AUTH_STATE_PREFERENCE", MODE_PRIVATE)
         .edit { putString("AUTH_STATE", authState.jsonSerializeString()) }
   }

   private fun cleanState() {
      context.getSharedPreferences("AUTH_STATE_PREFERENCE", MODE_PRIVATE)
         .edit { remove("AUTH_STATE") }
   }

   fun getRefreshedAuthToken(): String?{
      authState?.needsTokenRefresh = true
      return getAuthToken()
   }

}