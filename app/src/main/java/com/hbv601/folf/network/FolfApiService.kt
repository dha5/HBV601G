package com.hbv601.folf.network

import com.hbv601.folf.Entities.AccessToken
import com.hbv601.folf.Entities.CourseData
import com.hbv601.folf.Entities.UserCreds
import com.hbv601.folf.Entities.UserEntity
import com.hbv601.folf.Entities.UserRegisterCreds
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

private const val BASE_URL = "https://hbv601g-backend.onrender.com"

/**
 * Use the Retrofit builder to build a retrofit object using a kotlinx.serialization converter
 */
private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

/**
 * Retrofit service object for creating api calls
 */
interface FolfApiService {
    @POST("login")
    suspend fun doLogin(@Body userCreds: UserCreds): Response<AccessToken>

    @POST("register")
    suspend fun doRegister(@Body userRegisterCreds: UserRegisterCreds): Response<UserEntity>

    @GET("fields")
    suspend fun getFields(): Response<List<CourseData>>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object FolfApi {
    val retrofitService: FolfApiService by lazy {
        retrofit.create(FolfApiService::class.java)
    }
}