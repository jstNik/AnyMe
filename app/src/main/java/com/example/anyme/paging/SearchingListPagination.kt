package com.example.anyme.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingSource.LoadResult
import androidx.paging.PagingState
import com.example.anyme.api.MalApi
import com.example.anyme.domain.ui.MalListGridItem
import com.example.anyme.domain.ui.MalRankingListItem
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository
import javax.inject.Inject

class SearchingListPagination(
   private val malRepository: IMalRepository,
   private val searchQuery: String
): PagingSource<Int, MalListGridItem>() {

   override fun getRefreshKey(state: PagingState<Int, MalListGridItem>): Int? {
      val key = state.anchorPosition?.let { anchorPosition ->
         val anchorPage = state.closestPageToPosition(anchorPosition)
         anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
      }
      return key
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MalListGridItem> {
      val key = params.key ?: 0
      val prevKey = if (key > 0) key - 1 else null
      val offset = key * MalApi.SEARCHING_LIST_LIMIT
      Log.d("Query", searchQuery)
      try{
         val searchList = malRepository.search(searchQuery, offset)
         val nextKey = if (searchList.isNotEmpty()) key + 1 else null
         return LoadResult.Page(searchList, prevKey, nextKey)
      } catch(e: Exception){
         Log.e("$e", e.message, e)
         return LoadResult.Error(e)
      }

   }
}