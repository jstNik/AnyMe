package com.example.anyme.di

import android.content.Context
import androidx.room.Room
import com.example.anyme.api.BufferInterceptor
import com.example.anyme.api.JsoupNetworkManager
import com.example.anyme.api.MalApi
import com.example.anyme.api.MalInterceptor
import com.example.anyme.daos.MalDao
import com.example.anyme.db.MalDatabase
import com.example.anyme.api.HtmlScraper
import com.example.anyme.api.LiveChartInterceptor
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton

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
    fun providesUserAnimeListDao(db: MalDatabase): MalDao = db.userMalListDao

    /**********************************************************************************************/

    @Provides
    @Singleton
    fun providesBufferInterceptor(): BufferInterceptor = BufferInterceptor()

    @Provides
    @Singleton
    fun providesMalInterceptor(@ApplicationContext context: Context): MalInterceptor =
        MalInterceptor(context)

    @Provides
    @Singleton
    fun providesLiveChartInterceptor() = LiveChartInterceptor()


    @Provides
    @Singleton
    fun providesMalApi(malInterceptor: MalInterceptor, bufferInterceptor: BufferInterceptor): MalApi =
        Retrofit
            .Builder()
            .client(OkHttpClient
                .Builder()
                .addInterceptor(malInterceptor)
                .addInterceptor(bufferInterceptor)
                .build()
            ).baseUrl(MalApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create()

    /**********************************************************************************************/

    @Provides
    @Singleton
    fun providesNetworkManager(
        bufferInterceptor: BufferInterceptor,
        liveChartInterceptor: LiveChartInterceptor
    ): JsoupNetworkManager =
        JsoupNetworkManager(
            OkHttpClient
                .Builder()
                .addInterceptor(liveChartInterceptor)
                .addInterceptor(bufferInterceptor)
                .build()
        )

    @Provides
    @Singleton
    fun providesEpisodeInfoScraper(networkManager: JsoupNetworkManager): HtmlScraper =
        HtmlScraper(networkManager)

    @Provides
    @Singleton
    fun providesMalRepository(api: MalApi, dao: MalDao, scraper: HtmlScraper): IMalRepository =
        MalRepository(api, dao, scraper)

}