package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class HoleData (val id:Int,val hole_number:Int,val par:Int)