package com.example.anyme.repositories.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.remote.api.MalApi
import com.example.anyme.domain.ui.mal.MalListGridItem
import com.example.anyme.remote.Host
import com.example.anyme.repositories.IMalRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchingListPagination(
   private val malApi: MalApi,
   private val searchQuery: String,
   private val dispatcher: CoroutineDispatcher = Dispatchers.IO
): PagingSource<Int, MalAnime>() {

   override fun getRefreshKey(state: PagingState<Int, MalAnime>): Int? {
      val key = state.anchorPosition?.let { anchorPosition ->
         val anchorPage = state.closestPageToPosition(anchorPosition)
         anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
      }
      return key
   }

   override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MalAnime> {
      val key = params.key ?: 0
      val prevKey = if (key > 0) key - 1 else null
      val offset = key * MalApi.SEARCHING_LIST_LIMIT
      try{
         val searchList = withContext(dispatcher){
            val response = if (searchQuery.isBlank())
               malApi.retrieveSuggestions(offset)
            else malApi.search(searchQuery, offset)
            response.body()!!.data.map{
               it.malAnime.host = Host.Mal
               it.malAnime
            }
         }
         val nextKey = if (searchList.isNotEmpty()) key + 1 else null
         return LoadResult.Page(searchList, prevKey, nextKey)
      } catch(e: Exception){
         Log.e("$e", e.message, e)
         return LoadResult.Error(e)
      }

   }
}