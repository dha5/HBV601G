package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id:Long,
    val name:String?,
    val username:String, ) {

}