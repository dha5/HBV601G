package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class PlayerEntity(
    val id:Long?,
    val user_id:Long,
    val name:String,
    val game_id:Long
)
