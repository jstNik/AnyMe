package com.example.anyme.ui.renders

import androidx.compose.runtime.Composable
import com.example.anyme.utils.time.OffsetDateTime

interface MediaListItemRender: MediaRender {

   @Composable
   fun Compose(
      onClick: () -> Unit
   )
   interface OffsetDateTimeComparable: MediaListItemRender, Comparable<OffsetDateTimeComparable> {

      val offsetDateTime: OffsetDateTime?

      override fun compareTo(other: OffsetDateTimeComparable): Int = 0

   }
}