package com.example.anyme.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.anyme.activities.LoginActivity
import com.example.anyme.utils.Resource
import com.example.anyme.viewmodels.SettingsViewModel

@Composable
fun LoginScreen(viewModel: SettingsViewModel = hiltViewModel<SettingsViewModel>()) {

   val resource by viewModel.settings.collectAsStateWithLifecycle()

   when(resource.status){
      Resource.Status.Success -> {
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
                        context.login(resource.data!!.host)
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
      Resource.Status.Loading -> TODO()
      Resource.Status.Failure -> TODO()
   }


}