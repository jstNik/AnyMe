package com.example.anyme.remote

import androidx.core.net.toUri
import com.example.anyme.BuildConfig
import com.example.anyme.remote.api.MalApi
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.CodeVerifierUtil
import net.openid.appauth.ResponseTypeValues

enum class Host(val apiKey: String) {
   Mal(BuildConfig.MAL_API_KEY){
      override fun buildAuthorizationRequest(): AuthorizationRequest {
         val serviceConfig = AuthorizationServiceConfiguration(
            MalApi.AUTHORIZATION_URL.toUri(),
            MalApi.TOKEN_URL.toUri(), null, null
         )
         val codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier()
         return AuthorizationRequest.Builder(
            serviceConfig,
            apiKey,
            ResponseTypeValues.CODE,
            MalApi.CALLBACK_URL.toUri()
         ).setCodeVerifier(codeVerifier, codeVerifier, "plain").build()
      }

   },
   TheMovieDatabase(BuildConfig.TMDB_API_KEY){
      override fun buildAuthorizationRequest(): AuthorizationRequest {
         throw IllegalStateException("Unimplemented host")
      }

   },
   Unknown(""){
      override fun buildAuthorizationRequest(): AuthorizationRequest {
         throw IllegalStateException("Unknown host")
      }

   };

   abstract fun buildAuthorizationRequest(): AuthorizationRequest

   companion object{

      fun getEnum(string: String) = when (string) {
         Mal.toString() -> Mal
         TheMovieDatabase.toString() -> TheMovieDatabase
         else -> Unknown
      }

   }

}