package com.example.anyme.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.anyme.api.MalApi
import com.example.anyme.api.MalInterceptor
import com.example.anyme.daos.UserAnimeListDao
import com.example.anyme.db.AnimeDatabase
import com.example.anyme.repositories.UserAnimeListRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MalModule {

    @Provides
    @Singleton
    fun providesAnimeDatabase(@ApplicationContext context: Context): AnimeDatabase =
        Room
            .databaseBuilder(context, AnimeDatabase::class.java, "AnimeDatabase")
            .build()

    @Provides
    @Singleton
    fun providesMalApi(@ApplicationContext context: Context): MalApi =
        Retrofit
            .Builder()
            .client(
                OkHttpClient
                    .Builder()
                    .addInterceptor(MalInterceptor(context))
                    .build()
            )
            .baseUrl(MalApi.BASE_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
            .create()

    @Provides
    @Singleton
    fun providesUserAnimeListDao(db: AnimeDatabase) = db.userAnimeListDao

    @Provides
    @Singleton
    fun providesUserAnimeListRepository(api: MalApi, dao: UserAnimeListDao) =
        UserAnimeListRepository(api, dao)


}