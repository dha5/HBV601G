package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class ScoreData (val id:Long?,
                      val strokes:Long,
                      val player_id: Long,
                      val hole_id:Long,
                      val game_id:Long,
                      val timestamp:String?)
