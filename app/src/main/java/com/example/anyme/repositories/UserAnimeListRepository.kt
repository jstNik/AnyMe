package com.example.anyme.repositories

import com.example.anyme.api.MalApi
import com.example.anyme.daos.UserMalListDao
import javax.inject.Inject

class UserAnimeListRepository @Inject constructor (
    private val api: MalApi,
    private val dao: UserMalListDao
){



}