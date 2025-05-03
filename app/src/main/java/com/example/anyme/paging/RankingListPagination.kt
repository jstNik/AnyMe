package com.example.anyme.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anyme.api.MalApi
import com.example.anyme.domain.ui.MalRankingListItem
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository.RankingListType


open class RankingListPagination(
   private val malRepository: IMalRepository,
   private val rankingListType: RankingListType
): PagingSource<Int, MalRankingListItem>() {

   private var count: Int = 0

   override fun getRefreshKey(state: PagingState<Int, MalRankingListItem>): Int? {
      val key = state.anchorPosition?.let { anchorPosition ->
         val anchorPage = state.closestPageToPosition(anchorPosition)
         anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
      }
      return key
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MalRankingListItem> {
      val key = params.key ?: 0
      val prevKey = if (key > 0) key - 1 else null
      val offset = key * MalApi.RANKING_LIST_LIMIT
      try {
         val rankList = malRepository.fetchRankingLists(rankingListType, offset)
         count++
         Log.i("Pagination", "$rankingListType: $count")
         val nextKey = if (rankList.isNotEmpty()) key + 1 else null
         return LoadResult.Page<Int, MalRankingListItem>(rankList, prevKey, nextKey)
      } catch (e: Exception) {
         Log.e("Key", "$e")
         return LoadResult.Error<Int, MalRankingListItem>(e)
      }
   }

}