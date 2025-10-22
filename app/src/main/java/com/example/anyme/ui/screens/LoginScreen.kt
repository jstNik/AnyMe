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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.anyme.activities.LoginActivity

@Composable
fun LoginScreen() {
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