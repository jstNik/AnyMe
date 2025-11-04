package com.example.anyme.remote.api

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMovieDBApi {

   companion object {
      const val BASE_URL_V3 = "https://api.themoviedb.org/3"
   }

   @GET("/search/tv")
   suspend fun tvSearch(
      @Query("query") query: String,
      @Query("year") year: Int?,
      @Query("include_adult") includeAdult: String = "true",
      @Query("language") language: String = "en-US",
      @Query("page") page: Int = 1
   ): Response<String>

   @GET("/search/movie")
   suspend fun movieSearch(
      @Query("query") query: String,
      @Query("year") year: String?,
      @Query("include_adult") includeAdult: String = "true",
      @Query("language") language: String = "en-US",
      @Query("page") page: Int = 1
   ): Response<String>

   @GET("/tv/{series_id}/images")
   suspend fun downloadTvImages(
      @Path("series_id") seriesId: Int
   ): Response<String>

   @GET("/movie/{movie_id}/images")
   suspend fun downloadMovieImages(
      @Path("movie_id") movieId: Int
   ): Response<String>

}