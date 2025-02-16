package com.example.anyme.daos

import androidx.room.Dao
import androidx.room.Query
import com.example.anyme.domain.mal_db.MalAnimeDB

@Dao
interface UserMalListDao {

    @Query(
        """
        select * 
        from MalAnimeDB order by 
        case 
            when :orderBy = 'title' then title
        end
        """
    )
    fun extractMalList(orderBy: String): MalAnimeDB

}