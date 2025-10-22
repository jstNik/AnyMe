package com.example.anyme.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.anyme.BuildConfig
import com.example.anyme.remote.api.MalApi
import com.example.anyme.ui.theme.AnyMeTheme
import net.openid.appauth.AuthState
import net.openid.appauth.AuthorizationException
import net.openid.appauth.AuthorizationRequest
import net.openid.appauth.AuthorizationResponse
import net.openid.appauth.AuthorizationService
import net.openid.appauth.AuthorizationServiceConfiguration
import net.openid.appauth.CodeVerifierUtil
import net.openid.appauth.ResponseTypeValues
import org.json.JSONException
import androidx.core.net.toUri
import androidx.core.content.edit
import com.example.anyme.remote.interceptors.MAL_AUTH_STATE_NAME
import com.example.anyme.remote.interceptors.SP_FILE_NAME
import com.example.anyme.ui.screens.LoginScreen

class LoginActivity : AppCompatActivity() {
    private lateinit var authService: AuthorizationService
    private val clientId: String = BuildConfig.API_KEY
    private var authState: AuthState = AuthState()

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val ex = AuthorizationException.fromIntent(it.data!!)
                val result = AuthorizationResponse.fromIntent(it.data!!)
                authState.update(result, ex)
                if (ex != null) {
                    throw ex
                }
                val tokenRequest = result?.createTokenExchangeRequest()
                authService.performTokenRequest(tokenRequest!!) { res, exception ->
                    if (exception != null) {
                        authState.update(res, exception)
                        throw exception
                    }
                    authState.update(res, null)
                    authState.performActionWithFreshTokens(authService) { _, _, _ ->
                        persistState()
                         val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent{
            AnyMeTheme {
                LoginScreen()
            }
        }
        if (canRestoreState()) {
            authService = AuthorizationService(this)
            persistState()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    fun login() {
        val serviceConfig = AuthorizationServiceConfiguration(
           MalApi.AUTHORIZATION_URL.toUri(),
           MalApi.TOKEN_URL.toUri(), null, null
        )
        val codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier()
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
           MalApi.CALLBACK_URL.toUri()
        ).setCodeVerifier(codeVerifier, codeVerifier, "plain").build()
        authService = AuthorizationService(this)
        val intent = authService.getAuthorizationRequestIntent(authRequest)
        try {
            launcher.launch(intent)
        } catch (ex: AuthorizationException){
            // TODO
        }
    }


    private fun persistState() {
        application.getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE).edit {
           putString(MAL_AUTH_STATE_NAME, authState.jsonSerializeString())
        }
    }

    @Throws(ClassCastException::class)
    private fun canRestoreState(): Boolean {
        val jsonString =
            application.getSharedPreferences(SP_FILE_NAME, MODE_PRIVATE)
                .getString(MAL_AUTH_STATE_NAME, null)

        val restoreSuccessful = when {

            !jsonString.isNullOrEmpty() -> try {
                authState = AuthState.jsonDeserialize(jsonString)
                !authState.accessToken.isNullOrEmpty()
            } catch (e: JSONException) {
                false
            }

            else -> false
        }

        return restoreSuccessful
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}
