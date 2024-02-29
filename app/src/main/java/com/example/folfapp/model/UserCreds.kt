package com.example.folfapp.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserCreds(
    val username: String,
    val password: String
)