package com.hbv601.folf.Entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import com.hbv601.folf.Entities.User
@Serializable
data class AccessToken(
    @SerialName(value = "access_token")
    val accessToken: String,

    @SerialName(value = "user")
    val user: User
)