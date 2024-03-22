package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class GameData (val id:Int?,
                     val creator:Int?,
                     val datetime:String,
                     val name:String)