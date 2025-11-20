package com.example.anyme.data.repositories

import androidx.paging.PagingData
import com.example.anyme.data.visitors.MalAnimeRepositoryAcceptor
import com.example.anyme.data.visitors.RepositoryAcceptor
import com.example.anyme.domain.dl.MalAnimeWrapper
import com.example.anyme.domain.dl.MediaWrapper
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.local.db.MalDatabase
import com.example.anyme.local.db.MalOrderOption
import com.example.anyme.local.db.OrderOption
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class MalRepositoryBundle(
   val malAnime: MalAnime,
   val malRepository: MalRepository
): RepositoryBundle<MalAnime, MalRepository.MalRankingTypes, MyList.Status, MalOrderOption> {


   override suspend fun downloadUserMediaList() {
      malRepository.downloadUserMediaList()
   }

   override fun fetchMediaUserList(
      myListStatus: MyList.Status,
      orderOption: MalOrderOption,
      filter: String
   ): Flow<PagingData<MalAnimeRepositoryAcceptor>> =
      malRepository.fetchMediaUserList(myListStatus, orderOption, filter)

   override fun fetchSeasonalMedia(): Flow<List<MalAnimeRepositoryAcceptor>> =
      malRepository.fetchSeasonalMedia()

   override fun fetchRankingLists(type: MalRepository.MalRankingTypes): Flow<PagingData<MalAnimeRepositoryAcceptor>> =
      malRepository.fetchRankingLists(type)

   override fun search(searchQuery: String): Flow<PagingData<MalAnimeRepositoryAcceptor>> =
      malRepository.search(searchQuery)

   override fun fetchMediaDetails(): Flow<MalAnimeRepositoryAcceptor> =
      malRepository.fetchMediaDetails(malAnime)

}