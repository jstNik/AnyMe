package com.example.anyme.ui.composables

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun AnyMeScaffold(
   topBar: @Composable () -> Unit,
   content: @Composable (PaddingValues) -> Unit
){
   Scaffold(
      topBar = topBar,
      bottomBar = { },
      content = content
   )
}