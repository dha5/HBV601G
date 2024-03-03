package com.hbv601.folf.Entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AccessToken(
    @SerialName(value = "access_token")
    val accessToken: String
)