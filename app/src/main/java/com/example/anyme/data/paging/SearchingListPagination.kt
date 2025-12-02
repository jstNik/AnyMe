package com.example.anyme.data.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anyme.data.visitors.repositories.MalAnimeRepositoryAcceptor
import com.example.anyme.remote.api.MalApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchingListPagination(
   private val malApi: MalApi,
   private val searchQuery: String,
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
      val offset = key * MalApi.SEARCHING_LIST_LIMIT
      try{
         val searchList = withContext(dispatcher){
            val response = if (searchQuery.isBlank())
               malApi.retrieveSuggestions(offset)
            else malApi.search(searchQuery, offset)
            response.body()!!.data.map { it.media }
         }
         val nextKey = if (searchList.isNotEmpty()) key + 1 else null
         return LoadResult.Page(searchList, prevKey, nextKey)
      } catch(e: Exception){
         Log.e("$e", e.message, e)
         return LoadResult.Error(e)
      }

   }
}