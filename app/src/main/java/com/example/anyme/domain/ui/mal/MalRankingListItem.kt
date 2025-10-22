package com.example.anyme.domain.ui.mal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.Media
import com.example.anyme.remote.Host
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.composables.GridEntry

@Immutable
data class MalRankingListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val rank: Int = 0,
   val numListUsers: Int = 0,
   override val host: Host = Host.Unknown
) : Media