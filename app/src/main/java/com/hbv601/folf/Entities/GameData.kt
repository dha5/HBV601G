package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class GameData (val id:Long?,
                     val creator:Int?,
                     val datetime:String,
                     val name:String,val fieldId:Int?
    )

@Serializable
data class PostGameData(
    val game_name:String,
    val field_id: Long,
    val dateTime: String?,
    val invited_player_ids: ArrayList<Long>
)