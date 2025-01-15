package com.example.anyme.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.anyme.daos.UserAnimeListDao
import com.example.anyme.domain.mal.Anime


@Database(
    entities=[Anime::class],
    version=1,
    exportSchema=false
)
abstract class AnimeDatabase: RoomDatabase() {

    abstract val userAnimeListDao: UserAnimeListDao

}