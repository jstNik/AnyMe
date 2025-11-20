package com.example.anyme.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anyme.data.repositories.MalRepository
import com.example.anyme.remote.api.MalApi
import com.example.anyme.data.repositories.MalRepository.MalRankingTypes
import com.example.anyme.data.repositories.Repository
import com.example.anyme.data.visitors.MalAnimeRepositoryAcceptor
import com.example.anyme.domain.dl.MalAnimeWrapper
import com.example.anyme.domain.dl.MediaWrapper
import com.example.anyme.domain.dl.TypeRanking
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.dl.mal.MyList
import com.example.anyme.local.db.MalOrderOption
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


open class ExploreListPagination(
   private val malApi: MalApi,
   private val malRankingTypes: TypeRanking,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): PagingSource<Int, MalAnimeRepositoryAcceptor>() {

   override fun getRefreshKey(state: PagingState<Int, MalAnimeRepositoryAcceptor>): Int? {
      val key = state.anchorPosition?.let { anchorPosition ->
         val anchorPage = state.closestPageToPosition(anchorPosition)
         anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
      }
      return key
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MalAnimeRepositoryAcceptor> {
      val key = params.key ?: 0
      val prevKey = if (key > 0) key - 1 else null
      val offset = key * MalApi.RANKING_LIST_LIMIT
      try {
         val rankList = withContext(dispatcher) {
            val response = malApi.retrieveRankingList(
               (malRankingTypes as MalRankingTypes).apiValue,
               offset = offset
            )
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