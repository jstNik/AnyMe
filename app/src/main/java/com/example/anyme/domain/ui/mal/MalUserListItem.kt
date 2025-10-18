package com.example.anyme.domain.ui.mal

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.anyme.R
import com.example.anyme.domain.dl.Media
import com.example.anyme.domain.dl.mal.MainPicture
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.domain.dl.mal.NextEpisode
import com.example.anyme.domain.dl.mal.mapToMalAnimeListItem
import com.example.anyme.ui.renders.MediaListItemRender
import com.example.anyme.ui.composables.ListEntry
import com.example.anyme.ui.theme.AnyMeTheme
import com.example.anyme.utils.LocalDateTypeAdapter
import com.example.anyme.utils.OffsetDateTime
import com.example.anyme.utils.OffsetDateTimeAdapter
import com.example.anyme.utils.OffsetWeekTime
import com.example.anyme.utils.OffsetWeekTimeAdapter
import com.example.anyme.utils.RangeMap
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone

@Immutable
data class MalUserListItem(
   override val id: Int = 0,
   override val title: String = "",
   override val mainPicture: MainPicture = MainPicture(),
   val numEpisodes: Int = 0,
   val myListStatusNumEpisodesWatched: Int = 0,
   val myListStatus: MyList.Status = MyList.Status.Undefined,
   val status: MalAnime.AiringStatus = MalAnime.AiringStatus.Undefined,
   val episodesType: RangeMap<MalAnime.EpisodesType> = RangeMap(),
   val nextEp: NextEpisode = NextEpisode(),
   val hasNotificationsOn: Boolean = false
) : Media