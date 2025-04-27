package com.example.anyme.di

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import com.example.anyme.api.MalApi
import com.example.anyme.api.MalInterceptor
import com.example.anyme.daos.MalDao
import com.example.anyme.db.MalDatabase
import com.example.anyme.paging.RankingListPagination
import com.example.anyme.repositories.EpisodeInfoScraper
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import kotlin.time.Duration.Companion.seconds

@Module
@InstallIn(SingletonComponent::class)
object MalModule {

    @Provides
    @Singleton
    fun providesAnimeDatabase(@ApplicationContext context: Context): MalDatabase =
        Room
            .databaseBuilder(context, MalDatabase::class.java, "AnimeDatabase")
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
                    .retryOnConnectionFailure(false)
                    .build()
            )
            .baseUrl(MalApi.BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create()

    @Provides
    @Singleton
    fun providesUserAnimeListDao(db: MalDatabase) = db.userMalListDao

    @Provides
    @Singleton
    fun providesEpisodeInfoScraper(): EpisodeInfoScraper = EpisodeInfoScraper()

    @Provides
    @Singleton
    fun providesMalRepository(api: MalApi, dao: MalDao, scraper: EpisodeInfoScraper): IMalRepository =
        MalRepository(api, dao, scraper)

}