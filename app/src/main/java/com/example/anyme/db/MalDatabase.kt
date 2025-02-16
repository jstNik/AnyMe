package com.example.anyme.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.anyme.daos.UserMalListDao
import com.example.anyme.domain.mal_db.MalAnimeDB


@Database(
    entities=[MalAnimeDB::class],
    version=2,
    exportSchema=false
)
abstract class MalDatabase: RoomDatabase() {

    abstract val userMalListDao: UserMalListDao

}