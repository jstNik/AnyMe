package com.example.anyme.repositories.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anyme.domain.remote.mal.Data
import com.example.anyme.local.daos.MalDao
import com.example.anyme.remote.Host
import com.example.anyme.remote.api.MalApi
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository.RankingListType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


open class ExploreListPagination(
   private val malApi: MalApi,
   private val rankingListType: RankingListType,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): PagingSource<Int, Data>() {

   override fun getRefreshKey(state: PagingState<Int, Data>): Int? {
      val key = state.anchorPosition?.let { anchorPosition ->
         val anchorPage = state.closestPageToPosition(anchorPosition)
         anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
      }
      return key
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Data> {
      val key = params.key ?: 0
      val prevKey = if (key > 0) key - 1 else null
      val offset = key * MalApi.RANKING_LIST_LIMIT
      try {
         val rankList = withContext(dispatcher) {
            val response = malApi.retrieveRankingList(rankingListType.apiValue, offset = offset)
            response.body()!!.data.forEach { it.malAnime.host = Host.Mal }
            response.body()!!.data
         }
         val nextKey = if (rankList.isNotEmpty()) key + 1 else null
         return LoadResult.Page(rankList, prevKey, nextKey)
      } catch (e: Exception) {
         Log.e("$e", "${e.message}", e)
         return LoadResult.Error(e)
      }
   }

}