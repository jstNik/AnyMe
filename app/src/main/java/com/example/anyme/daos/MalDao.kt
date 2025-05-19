package com.example.anyme.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.anyme.domain.mal_db.MalAnimeDB
import com.example.anyme.domain.mal_dl.MyList

@Dao
interface MalDao {

   companion object {
      private const val QUERY: String = """
        select *
        from MalAnimeDB
        where myListStatusStatus = :myListStatus and title like '%' || :filter || '%'
        order by
            case
                when :orderBy = 'Title' then title
                when :orderBy = 'LastUpdateAt' then myListStatusUpdatedAt
            end
        """
   }

   @Insert
   suspend fun insert(anime: MalAnimeDB): Long

   @Update
   suspend fun update(anime: MalAnimeDB): Int

   @Delete
   suspend fun delete(anime: List<MalAnimeDB>): Int

   // FIXME Dummy query to build the app
   @Suppress("ConvertToStringTemplate")
   @Query(QUERY + " ASC")
   fun fetchUserAnimeAsc(
      myListStatus: String,
      orderBy: String,
      filter: String
   ): PagingSource<Int, MalAnimeDB>

   @Suppress("ConvertToStringTemplate")
   @Query(QUERY + " DESC")
   fun fetchUserAnimeDesc(
      myListStatus: String,
      orderBy: String,
      filter: String
   ): PagingSource<Int, MalAnimeDB>

   @Query("SELECT * FROM MalAnimeDB ORDER BY id ASC")
   fun fetchAnimeIds(): List<MalAnimeDB>
}