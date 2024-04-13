package com.honeyapps.wordlebattles.network

import com.honeyapps.wordlebattles.utils.env
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit

// for the OkHttpClient, to add an Authorization header to each request with a runtime api key from .env
object AuthorizationInterceptor : Interceptor {
    private val PYAPI_KEY: String = env["PYAPI_KEY"]
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val modifiedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $PYAPI_KEY")
            .build()
        return chain.proceed(modifiedRequest)
    }
}

// share different OkHttpClient based on auth param
object MyHttpClient {
    fun httpClient(auth: Boolean = false): OkHttpClient =
        if (auth)
            OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor)
                .build()
        else
            OkHttpClient()
}

// factory to create Retrofit Builder objects
object RetrofitInstanceFactory {
    fun createInstance(baseUrl: String): Retrofit {
        try {
            return Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(MyHttpClient.httpClient(auth = true))
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
        } catch (e: Exception) {
            throw RuntimeException("Error creating Retrofit instance for baseUrl $baseUrl", e)
        }
    }
}