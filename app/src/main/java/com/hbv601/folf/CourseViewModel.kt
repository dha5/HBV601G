package com.hbv601.folf

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hbv601.folf.Entities.CourseData
import com.hbv601.folf.network.FolfApi

data class Course(
    val name: String,
    val distance: String,
    val games: List<Game>
)

data class Game(
    val name: String,
    val dateTime: String,
    val registeredPlayers: Int,
    val creatorName: String
)

class CourseViewModel : ViewModel() {

    private val _courses = MutableLiveData<List<CourseData>?>()
    var courses: MutableLiveData<List<CourseData>?> = _courses

    fun getCourseName(position: Int): String? {
        return courses.value?.getOrNull(position)?.name
    }


     suspend fun fetchCourses(): List<CourseData>? {

        // þarf að bæta við aðferð til að sækja gögn úr bakenda
       val resCourses = FolfApi.retrofitService.getFields()

            val s = resCourses.body()

            _courses.postValue(s)




        return s
    }
}