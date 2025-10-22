package com.example.anyme.activities

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.anyme.ui.composables.AnyMeScaffold
import com.example.anyme.ui.theme.AnyMeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      enableEdgeToEdge()
      setContent {
         AnyMeTheme(darkTheme = true) {
            AnyMeScaffold()
         }
      }
   }

}