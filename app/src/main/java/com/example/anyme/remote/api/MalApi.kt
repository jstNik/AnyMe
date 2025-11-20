package com.example.anyme.remote.api

import com.example.anyme.domain.dl.mal.MalAnime
import com.example.anyme.domain.remote.mal.MalAnimeListGetResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MalApi {


   @GET("users/@me/animelist")
   suspend fun retrieveUserAnimeList(
      @Query("limit") limit: Int = USER_LIST_LIMIT,
      @Query("offset") offset: Int = 0,
      @Query("nsfw") nsfw: String = "true",
      @Query("fields") fields: String = USER_LIST_FIELDS.joinToString(",") { getFieldName(it.name) }

//        @Query("sort") sort: String = "anime_id"
   ): Response<MalAnimeListGetResponse>


   @GET("anime/ranking")
   suspend fun retrieveRankingList(
      @Query("ranking_type") type: String,
      @Query("limit") limit: Int = RANKING_LIST_LIMIT,
      @Query("offset") offset: Int = 0,
      @Query("nsfw") nsfw: String = "true",
      @Query("fields") fields: String = RANKING_LIST_FIELDS.joinToString(",") { getFieldName(it.name) }
   ): Response<MalAnimeListGetResponse>

   @GET("anime/season/{year}/{season}")
   suspend fun retrieveSeasonalAnimes(
      @Path("year") year: Int,
      @Path("season") season: String,
      @Query("offset") offset: Int = 0,
      @Query("limit") limit: Int = SEASONAL_LIST_LIMIT,
      @Query("sort") sort: String = "anime_score",
      @Query("nsfw") nsfw: String = "true",
      @Query("fields") fields: String = SEASON_LIST_FIELDS.joinToString(",") { getFieldName(it.name) }
   ): Response<MalAnimeListGetResponse>

   @GET("anime")
   suspend fun search(
      @Query("q") title: String,
      @Query("offset") offset: Int = 0,
      @Query("limit") limit: Int = SEARCHING_LIST_LIMIT,
      @Query("fields") fields: String = SEARCH_FIELDS.joinToString(",") { getFieldName(it.name) }
   ): Response<MalAnimeListGetResponse>

   @GET("anime/suggestions")
   suspend fun retrieveSuggestions(
      @Query("offset") offset: Int = 0,
      @Query("limit") limit: Int = SEARCHING_LIST_LIMIT,
      @Query("fields") fields: String = SEARCH_FIELDS.joinToString(",") { getFieldName(it.name) }
   ): Response<MalAnimeListGetResponse>

   @GET("anime/{anime_id}")
   suspend fun retrieveAnimeDetails(
      @Path("anime_id") id: Int,
      @Query("fields") fields: String = DETAILS_FIELDS.joinToString(",") { getFieldName(it.name) }
   ): Response<MalAnime>

   companion object {
      const val CALLBACK_URL = "animetracker://auth.io" // TODO Change redirect url
      const val AUTHORIZATION_URL = "https://myanimelist.net/v1/oauth2/authorize"
      const val TOKEN_URL = "https://myanimelist.net/v1/oauth2/token"
      const val BASE_URL = "https://api.myanimelist.net/v2/"

      const val USER_LIST_LIMIT = 1000
      const val RANKING_LIST_LIMIT = 30
      const val SEARCHING_LIST_LIMIT = 50
      const val SEASONAL_LIST_LIMIT = 500

      private val API_FIELD_MAP = mapOf(
         MalAnime::mainPicture.name to "main_picture",
         MalAnime::numListUsers.name to "num_list_users",
         MalAnime::numEpisodes.name to "num_episodes",
         MalAnime::myList.name to "my_list_status",
         MalAnime::endDate.name to "end_date",
         MalAnime::startDate.name to "start_date",
         MalAnime::alternativeTitles.name to "alternative_titles",
         MalAnime::numScoringUsers.name to "num_scoring_users",
         MalAnime::createdAt.name to "created_at",
         MalAnime::updatedAt.name to "updated_at",
         MalAnime::mediaType.name to "media_type",
         MalAnime::season.name to "start_season",
         MalAnime::averageEpisodeDuration.name to "average_episode_duration",
         MalAnime::relatedAnime.name to "related_anime"
      )

      private fun getFieldName(parameterName: String) =
         API_FIELD_MAP[parameterName] ?: parameterName

      private val BASIC_FIELDS = setOf(
         MalAnime::id,
         MalAnime::title,
         MalAnime::mainPicture
      )
      val RANKING_LIST_FIELDS = BASIC_FIELDS + setOf(
         MalAnime::popularity,
         MalAnime::numListUsers,
         MalAnime::mean
      )
      val SEARCH_FIELDS = BASIC_FIELDS + RANKING_LIST_FIELDS + setOf(
         MalAnime::myList
      )
      val USER_LIST_FIELDS = BASIC_FIELDS + setOf(
         MalAnime::alternativeTitles,
         MalAnime::startDate,
         MalAnime::endDate,
         MalAnime::synopsis,
         MalAnime::mean,
         MalAnime::rank,
         MalAnime::popularity,
         MalAnime::numListUsers,
         MalAnime::numScoringUsers,
         MalAnime::nsfw,
         MalAnime::genres,
         MalAnime::createdAt,
         MalAnime::updatedAt,
         MalAnime::mediaType,
         MalAnime::status,
         MalAnime::myList,
         MalAnime::numEpisodes,
         MalAnime::season,
         MalAnime::broadcast,
         MalAnime::source,
         MalAnime::averageEpisodeDuration,
         MalAnime::rating,
         MalAnime::studios
      )
      val SEASON_LIST_FIELDS = BASIC_FIELDS + setOf(
         MalAnime::broadcast,
         MalAnime::mean,
         MalAnime::endDate,
         MalAnime::startDate,
         MalAnime::broadcast
      )
      val DETAILS_FIELDS = BASIC_FIELDS + setOf(
         MalAnime::alternativeTitles,
         MalAnime::startDate,
         MalAnime::endDate,
         MalAnime::synopsis,
         MalAnime::mean,
         MalAnime::rank,
         MalAnime::popularity,
         MalAnime::numListUsers,
         MalAnime::numScoringUsers,
         MalAnime::nsfw,
         MalAnime::genres,
         MalAnime::createdAt,
         MalAnime::updatedAt,
         MalAnime::mediaType,
         MalAnime::status,
         MalAnime::myList,
         MalAnime::numEpisodes,
         MalAnime::season,
         MalAnime::broadcast,
         MalAnime::source,
         MalAnime::averageEpisodeDuration,
         MalAnime::rating,
         MalAnime::studios,
         MalAnime::pictures,
         MalAnime::background,
         MalAnime::relatedAnime,
//         "related_manga"
         MalAnime::recommendations,
         MalAnime::statistics
      )

   }


}