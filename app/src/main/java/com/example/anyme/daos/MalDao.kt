package com.example.anyme.daos

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.anyme.domain.mal_db.MalAnimeDB

@Dao
interface MalDao {

    @Insert
    suspend fun insert(anime: MalAnimeDB): Long

    @Update
    suspend fun update(anime: MalAnimeDB): Int

    @Delete
    suspend fun delete(anime: List<MalAnimeDB>): Int

    // FIXME Dummy query to build the app
    @Query(
        """
        select *
        from MalAnimeDB
        where :orderDirection = 'Asc' and :filter = ''
        order by
            case
                when :orderBy = 'Title' then title
                when :orderBy = 'LastUpdateAt' then myListStatusUpdatedAt
            end

        """
    )
    fun fetchUserAnime(
       orderBy: String,
       orderDirection: String,
       filter: String = ""
    ): PagingSource<Int, MalAnimeDB>

    @Query("SELECT * FROM MalAnimeDB ORDER BY id ASC")
    fun fetchAnimeIds(): List<MalAnimeDB>
}