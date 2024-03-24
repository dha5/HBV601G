package com.hbv601.folf.network

import com.hbv601.folf.Entities.AccessToken
import com.hbv601.folf.Entities.CourseData
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.Entities.HoleData
import com.hbv601.folf.Entities.PlayerEntity
import com.hbv601.folf.Entities.PostGameData
import com.hbv601.folf.Entities.RegisterUser
import com.hbv601.folf.Entities.ResponseMessage
import com.hbv601.folf.Entities.User
import com.hbv601.folf.Entities.ScoreParcel
import com.hbv601.folf.Entities.UserCreds
import com.hbv601.folf.Entities.UserEntity
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

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
    suspend fun doRegister(@Body user: RegisterUser): Response<User>

    @GET("fields")
    suspend fun getFields(): Response<List<CourseData>>

    @GET("holes/field/{id}")
    suspend fun getHolesByFieldId(
        @Path("id") fieldId:Int): Response<List<HoleData>>

    //game functions


    @GET("scores/game/{id}")
    suspend fun getScoreByGameId(
        @Path("id") gameId: Int
    ): Response<ScoreParcel>
    

    @GET("games/field/{id}")
    suspend fun getGamesByFieldId(
        @Path("id") fieldId:Int):Response<List<GameData>>

    @GET("games")
    suspend fun getLoggedInUserGames(
        @Header("Authorization") token: String
    ):Response<List<GameData>>

    @GET("games/{gameid}")
    suspend fun getGameById(@Path("gameid") gameid: Long):Response<GameData>
    @POST("games")
    suspend fun createGame(
        @Header("Authorization") BearerToken:String,@Body data:PostGameData
    ):Response<GameData>

    @PUT("games/{gameid}")
    suspend fun updateGame(@Path("gameid") gameid:Long,
                           @Body data:GameData):Response<ResponseMessage>
    @GET("PastGames")
    suspend fun getYourPastGames(
        @Header("Authorization") BearerToken:String
    ):Response<List<GameData>>

    @POST("startGame")
    suspend fun startGame(
        @Body id:Int
    )
    @POST("endGame")
    suspend fun endGame(
        @Body id:Int
    )
    @GET("/players/gameid/{game_id}")
    suspend fun getGamePlayers(@Path("game_id")gameid:Long):Response<List<PlayerEntity>>
    @POST("players")
    suspend fun addPlayer(@Header("Authorization")BearerToken: String,@Body data: PlayerEntity):Response<PlayerEntity>

    @DELETE("players/{player_id}")
    suspend fun removePlayer(@Path("player_id")playerid:Long):Response<ResponseMessage>

    //friends functions
    @GET("friends")
    suspend fun getFriends(@Header("Authorization") BearerToken: String):Response<List<UserEntity>>
    @GET("allusers")
    suspend fun getUsers():Response<List<UserEntity>>
    @POST("friends")
    suspend fun addFriend(@Header("Authorization") BearerToken: String, @Body data:UserEntity):Response<ResponseMessage>
    @POST("friends/delete")
    suspend fun deleteFriend(@Header("Authorization")BearerToken:String, @Body data: UserEntity):Response<ResponseMessage>
}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object FolfApi {
    val retrofitService: FolfApiService by lazy {
        retrofit.create(FolfApiService::class.java)
    }
}