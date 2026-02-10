package com.example.anyme.local.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import androidx.room.Upsert
import com.example.anyme.domain.local.mal.MalAnimeDB
import kotlinx.coroutines.flow.Flow

@Dao
interface MalDao {

   companion object {
      private const val QUERY: String = """
        SELECT *
        FROM MalAnimeDB
        WHERE myListStatusStatus = :myListStatus AND title LIKE '%' || :filter || '%'
        ORDER BY
            CASE
                WHEN :orderBy = 'Title' THEN title
                WHEN :orderBy = 'LastUpdateAt' THEN myListStatusUpdatedAt
            END
        """
   }

   @Insert
   suspend fun insert(anime: MalAnimeDB): Long

   @Update
   suspend fun update(anime: MalAnimeDB): Int

   @Upsert
   @Transaction
   suspend fun upsert(animes: List<MalAnimeDB>): List<Long>

   @Delete
   @Transaction
   suspend fun delete(anime: List<MalAnimeDB>): Int

   // FIXME Dummy query to build the app
   @Query("$QUERY ASC")
   fun fetchUserAnimeAsc(
      myListStatus: String,
      orderBy: String,
      filter: String
   ): PagingSource<Int, MalAnimeDB>

   @Query("$QUERY DESC")
   fun fetchUserAnimeDesc(
      myListStatus: String,
      orderBy: String,
      filter: String
   ): PagingSource<Int, MalAnimeDB>

   @Query("SELECT * FROM MalAnimeDB")
   fun fetchAnimeIds(): List<MalAnimeDB>

   @Query("SELECT * FROM MalAnimeDB WHERE id = :animeId")
   fun getAnimeById(animeId: Int): Flow<MalAnimeDB?>

}