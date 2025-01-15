package com.example.anyme.api

import com.example.anyme.domain.mal.UserListAnime
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface MalApi {

    companion object{
        const val CALLBACK_URL = "animetracker://auth.io" // TODO Change redirect url
        const val AUTHORIZATION_URL = "https://myanimelist.net/v1/oauth2/authorize"
        const val TOKEN_URL = "https://myanimelist.net/v1/oauth2/token"
        const val BASE_URL= "https://api.myanimelist.net/v2/"
        const val LIMIT = 1000
        const val FIELDS = """
            "id,title,main_picture,alternative_titles,start_date,end_date,synopsis,mean,rank,
            popularity,num_list_users,num_scoring_users,nsfw,created_at,updated_at,media_type,
            status,genres,my_list_status,num_episodes,start_season,broadcast,source,
            average_episode_duration,rating,pictures,background,related_anime,related_manga,
            recommendations,studios,statistics"
        """
    }

    @GET("users/@me/animelist")
    suspend fun retrieveUserAnimeList(
        @Query("fields") fields: String = FIELDS,
        @Query("limit") limit: Int = LIMIT,
        @Query("offset") offset: Int = 0,
        @Query("nsfw") nsfw: String = "true"
    ): Response<UserListAnime>

}