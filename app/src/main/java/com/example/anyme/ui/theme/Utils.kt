package com.example.anyme.ui.theme

import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.ui.unit.TextUnit

fun TextUnit.stepBased(factor: Double) = if (factor < 1.0)
   TextAutoSize.StepBased(this * factor, this) else
      TextAutoSize.StepBased(this, this * factor)