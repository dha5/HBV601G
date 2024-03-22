package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class RegisterUser(
    val name: String,
    val username: String,
    val password: String
)