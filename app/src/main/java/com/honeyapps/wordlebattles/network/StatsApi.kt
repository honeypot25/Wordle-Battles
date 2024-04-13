package com.honeyapps.wordlebattles.network

import com.honeyapps.wordlebattles.data.models.StatsModel
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Query

private const val API_BASE_URL = "https://honeyapps.eu.pythonanywhere.com"

interface StatsApiService {

    @POST("/api/stats/create")
    suspend fun createStats(
        @Query("uid") uid: String
    ): Response<Boolean>

    @GET("/api/stats/get")
    suspend fun getStats(
        @Query("uid") uid: String
    ): Response<StatsModel>

    @PUT("/api/stats/update")
    suspend fun updateStats(
        @Query("uid") uid: String,
        @Query("matchId") matchId: String,
        @Body providedMatch: Map<String, Int>
    ): Response<Boolean>

    @DELETE("/api/stats/delete")
    suspend fun deleteStats(
        @Query("uid") uid: String
    ): Response<Boolean>
}

object StatsApi {
    private val retrofit by lazy {
        RetrofitInstanceFactory.createInstance(
            baseUrl = API_BASE_URL,
        )
    }
    private val _statsApiService: StatsApiService by lazy {
        retrofit.create(StatsApiService::class.java)
    }
    // public getter
    val statsApiService: StatsApiService
        get() = _statsApiService
}