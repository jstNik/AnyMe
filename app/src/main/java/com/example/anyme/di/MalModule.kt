package com.example.anyme.di

import android.content.Context
import androidx.room.Room
import com.example.anyme.remote.interceptors.BufferInterceptor
import com.example.anyme.remote.scrapers.JsoupHtmlCacher
import com.example.anyme.remote.api.MalApi
import com.example.anyme.remote.interceptors.MalInterceptor
import com.example.anyme.local.daos.MalDao
import com.example.anyme.local.db.MalDatabase
import com.example.anyme.remote.scrapers.HtmlScraper
import com.example.anyme.remote.interceptors.LiveChartInterceptor
import com.example.anyme.remote.token_managers.MalTokenManager
import com.example.anyme.repositories.IMalRepository
import com.example.anyme.repositories.MalRepository
import com.example.anyme.utils.OffsetDateTimeAdapter
import com.example.anyme.utils.DateTypeAdapter
import com.example.anyme.remote.interceptors.MAL_AUTH_STATE_NAME
import com.example.anyme.remote.interceptors.SP_FILE_NAME
import com.example.anyme.remote.interceptors.ValidationInterceptor
import com.example.anyme.repositories.SettingsRepository
import com.example.anyme.utils.time.OffsetDateTime
import com.example.anyme.utils.time.OffsetWeekTime
import com.example.anyme.utils.OffsetWeekTimeAdapter
import com.example.anyme.utils.time.Date
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
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
   fun providesValidationInterceptor(): ValidationInterceptor = ValidationInterceptor()

    @Provides
    @Singleton
    fun providesMalTokenManager(@ApplicationContext context: Context) =
        MalTokenManager(context, SP_FILE_NAME, MAL_AUTH_STATE_NAME)

    @Provides
    @Singleton
    fun providesMalInterceptor(tokenManager: MalTokenManager): MalInterceptor =
        MalInterceptor(tokenManager)

    @Provides
    @Singleton
    fun providesLiveChartInterceptor() = LiveChartInterceptor()

    @Provides
    @Singleton
    fun providesGson(): Gson = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateTypeAdapter())
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
        .registerTypeAdapter(OffsetWeekTime::class.java, OffsetWeekTimeAdapter(TimeZone.of("Asia/Tokyo")))
        .create()


    @Provides
    @Singleton
    fun providesMalApi(
        gson: Gson,
        malInterceptor: MalInterceptor,
        bufferInterceptor: BufferInterceptor,
        validationInterceptor: ValidationInterceptor
    ): MalApi =
        Retrofit
            .Builder()
            .client(OkHttpClient
                .Builder()
                .addInterceptor(malInterceptor)
                .addInterceptor(bufferInterceptor)
               .addInterceptor(validationInterceptor)
                .build()
            ).baseUrl(MalApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create()

    /**********************************************************************************************/

    @Provides
    @Singleton
    fun providesNetworkManager(
        bufferInterceptor: BufferInterceptor,
        liveChartInterceptor: LiveChartInterceptor
    ): JsoupHtmlCacher =
        JsoupHtmlCacher(
            OkHttpClient
                .Builder()
                .addInterceptor(liveChartInterceptor)
                .addInterceptor(bufferInterceptor)
                .build()
        )

    @Provides
    @Singleton
    fun providesEpisodeInfoScraper(networkManager: JsoupHtmlCacher): HtmlScraper =
        HtmlScraper(networkManager)

    @Provides
    @Singleton
    fun providesMalRepository(api: MalApi, dao: MalDao, gson: Gson, scraper: HtmlScraper): IMalRepository =
        MalRepository(api, dao, scraper, gson)

    @Provides
    @Singleton
    fun providesSettingsRepository(@ApplicationContext context: Context) = SettingsRepository(context)

}