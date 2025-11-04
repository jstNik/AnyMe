package com.example.anyme.di

import android.content.Context
import com.example.anyme.remote.api.TheMovieDBApi
import com.example.anyme.remote.interceptors.TmdbInterceptor
import com.example.anyme.remote.interceptors.ValidationInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TmdbModule {

   @Singleton
   @Provides
   fun providesTmdbInterceptor() = TmdbInterceptor()

   @Singleton
   @Provides
   fun providesTmdbApi(
      tmdbInterceptor: TmdbInterceptor,
      validationInterceptor: ValidationInterceptor
   ): TheMovieDBApi = Retrofit
      .Builder()
      .client(
         OkHttpClient
            .Builder()
            .addInterceptor(tmdbInterceptor)
            .addInterceptor(validationInterceptor)
            .build()
      )
      .baseUrl(TheMovieDBApi.BASE_URL_V3)
      .build()
      .create()


}