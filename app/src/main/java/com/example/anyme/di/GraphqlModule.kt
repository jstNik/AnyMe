package com.example.anyme.di

import com.apollographql.apollo.ApolloClient
import com.example.anyme.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class AniListClient

@InstallIn(SingletonComponent::class)
@Module
object GraphqlModule {

   @AniListClient
   @Singleton
   @Provides
   fun providesApolloClient() =
      ApolloClient.Builder().serverUrl(BuildConfig.ANILIST_ENDPOINT).build()


}