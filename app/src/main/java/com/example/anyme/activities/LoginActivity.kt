package com.example.anyme.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.auth0.android.jwt.JWT
import com.example.anyme.BuildConfig
import com.example.anyme.api.MalApi
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

class LoginActivity : AppCompatActivity() {
    private lateinit var authService: AuthorizationService
    private lateinit var jwt: JWT
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
                    jwt = JWT(res!!.accessToken!!)
                    authState.performActionWithFreshTokens(authService) { _, _, _ ->
                        persistState()
                         val intent = Intent(this, UserAnimeListActivity::class.java)
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
                ComposeLoginUi()
            }
        }
        if (canRestoreState()) {
            authService = AuthorizationService(this)
            persistState()
            val intent = Intent(this, UserAnimeListActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


    fun login() {
        val serviceConfig = AuthorizationServiceConfiguration(
            Uri.parse(MalApi.AUTHORIZATION_URL),
            Uri.parse(MalApi.TOKEN_URL), null, null
        )
        val codeVerifier = CodeVerifierUtil.generateRandomCodeVerifier()
        val authRequest = AuthorizationRequest.Builder(
            serviceConfig,
            clientId,
            ResponseTypeValues.CODE,
            Uri.parse(MalApi.CALLBACK_URL)
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
        application.getSharedPreferences("AUTH_STATE_PREFERENCE", MODE_PRIVATE).edit()
            .putString("AUTH_STATE", authState.jsonSerializeString()).apply()
    }

    @Throws(ClassCastException::class)
    private fun canRestoreState(): Boolean {
        val jsonString =
            application.getSharedPreferences("AUTH_STATE_PREFERENCE", MODE_PRIVATE)
                .getString("AUTH_STATE", null)

        val restoreSuccessful = when {

            !jsonString.isNullOrEmpty() -> try {
                authState = AuthState.jsonDeserialize(jsonString)
                !authState.accessToken.isNullOrEmpty()
            } catch (e: JSONException) {
                false
            }

            else -> false
        }

        if (restoreSuccessful)
            jwt = JWT(authState.accessToken!!)

        return restoreSuccessful
    }

    override fun onDestroy() {
        super.onDestroy()
        authService.dispose()
    }
}

@Composable
fun ComposeLoginUi() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        val context = LocalContext.current

        Button(
            onClick = {
                try {
                    if (context is LoginActivity)
                        context.login()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Something went wrong. Try later.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        ) {
            Text(
                text = "Login"
            )
        }
    }

}
