package com.hbv601.folf.Entities

import kotlinx.serialization.Serializable

@Serializable
data class CourseData
    (val id:Int,
     val name:String,
     val description:String,
     val location:String)