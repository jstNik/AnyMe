package com.example.anyme.api

import com.example.anyme.domain.mal_api.MalAnimeListGetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MalApi {

   companion object {
      const val CALLBACK_URL = "animetracker://auth.io" // TODO Change redirect url
      const val AUTHORIZATION_URL = "https://myanimelist.net/v1/oauth2/authorize"
      const val TOKEN_URL = "https://myanimelist.net/v1/oauth2/token"
      const val BASE_URL = "https://api.myanimelist.net/v2/"

      const val USER_LIST_LIMIT = 1000
      const val RANKING_LIST_LIMIT = 30
      const val SEARCHING_LIST_LIMIT = 50
      const val SEASONAL_LIST_LIMIT = 500

      val BASIC_FIELDS = setOf(
         "id", "title", "main_picture"
      )
      val RANKING_LIST_FIELDS = setOf(
         "popularity", "rating", "num_list_users", "num_episodes", "status", "mean"
      )
      val USER_LIST_FIELDS = setOf(
         "my_list_status"
      )
      val SEASON_LIST_FIELDS = setOf(
         "broadcast", "mean", "end_date", "start_date"
      )
      val DETAILS_FIELDS = setOf(
         "alternative_titles", "synopsis", "rank", "popularity", "num_scoring_users", "nsfw",
         "created_at", "updated_at", "media_type", "genres", "start_season", "source",
         "average_episode_duration", "rating", "pictures", "background", "related_anime",
         "related_manga", "recommendations", "studios", "statistics"
      )

   }

   @GET("users/@me/animelist")
   suspend fun retrieveUserAnimeList(
      @Query("limit") limit: Int = USER_LIST_LIMIT,
      @Query("offset") offset: Int = 0,
      @Query("nsfw") nsfw: String = "true",
      @Query("fields") fields: String = (
              BASIC_FIELDS + USER_LIST_FIELDS + RANKING_LIST_FIELDS + SEASON_LIST_FIELDS + DETAILS_FIELDS
              ).joinToString(",")
//        @Query("sort") sort: String = "anime_id"
   ): Response<MalAnimeListGetResponse>


   @GET("anime/ranking")
   suspend fun retrieveRankingList(
      @Query("ranking_type") type: String,
      @Query("limit") limit: Int = RANKING_LIST_LIMIT,
      @Query("offset") offset: Int = 0,
      @Query("nsfw") nsfw: String = "true",
      @Query("fields") fields: String = (BASIC_FIELDS + RANKING_LIST_FIELDS).joinToString(",")
   ): Response<MalAnimeListGetResponse>

   @GET("anime/season/{year}/{season}")
   suspend fun retrieveSeasonalAnimes(
      @Path("year") year: Int,
      @Path("season") season: String,
      @Query("offset") offset: Int = 0,
      @Query("limit") limit: Int = SEASONAL_LIST_LIMIT,
      @Query("sort") sort: String = "anime_score",
      @Query("nsfw") nsfw: String = "true",
      @Query("fields") fields: String = (BASIC_FIELDS + SEASON_LIST_FIELDS).joinToString(",")
   ): Response<MalAnimeListGetResponse>

   @GET("anime")
   suspend fun search(
      @Query("q") title: String,
      @Query("offset") offset: Int = 0,
      @Query("limit") limit: Int = SEARCHING_LIST_LIMIT,
      @Query("fields") fields: String = (BASIC_FIELDS + RANKING_LIST_FIELDS + USER_LIST_FIELDS).joinToString(",")
      ): Response<MalAnimeListGetResponse>

   @GET("anime/suggestions")
   suspend fun retrieveSuggestions(
      @Query("offset") offset: Int = 0,
      @Query("limit") limit: Int = SEARCHING_LIST_LIMIT,
      @Query("fields") fields: String = (BASIC_FIELDS + RANKING_LIST_FIELDS + USER_LIST_FIELDS).joinToString(",")
   ): Response<MalAnimeListGetResponse>

   @GET("anime/{anime_id}")
   suspend fun retrieveAnimeDetails(
      @Path("anime_id") id: Int,
      @Query("fields") fields: String = (
         BASIC_FIELDS + USER_LIST_FIELDS + RANKING_LIST_FIELDS + SEASON_LIST_FIELDS + DETAILS_FIELDS
         ).joinToString(",")
   ): Response<MalAnimeListGetResponse>


}