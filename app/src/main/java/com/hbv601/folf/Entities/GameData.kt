package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable

data class GameData (
    val id:Long?,
    val creator:Int?,
    val date_created:String,
    val name:String,
    val field_id:Int?
)

@Serializable
data class PostGameData(
    val game_name:String,
    val field_id: Long,
    val date_created: String?,
    val invited_player_ids: ArrayList<Long>
)