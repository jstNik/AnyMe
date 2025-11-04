package com.example.anyme.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope

@Module
@InstallIn(SingletonComponent::class)
object CoroutineModule {

   @Provides
   fun providesIoDispatcher() = Dispatchers.IO

   @Provides
   @Singleton
   @ApplicationScope
   fun providesApplicationScope(ioDispatcher: CoroutineDispatcher) =
      CoroutineScope(SupervisorJob() + ioDispatcher)

}