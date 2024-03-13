package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class UserRegisterCreds(
    val name:String?,
    val username:String,
    val password:String
)
