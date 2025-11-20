package com.example.anyme.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.anyme.local.daos.MalDao
import com.example.anyme.domain.local.mal.MalAnimeDB


@Database(
   entities = [MalAnimeDB::class],
   version = 4,
   exportSchema = false
)
abstract class MalDatabase : RoomDatabase() {

   abstract val userMalListDao: MalDao


}