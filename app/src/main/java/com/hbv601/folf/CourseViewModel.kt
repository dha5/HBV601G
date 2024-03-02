package com.hbv601.folf

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

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

    private val _courses = MutableLiveData<List<Course>>()
    val courses: LiveData<List<Course>> = _courses

    fun getCourseName(position: Int): String? {
        return courses.value?.getOrNull(position)?.name
    }

    fun getGameInfo(coursePosition: Int, gameIndex: Int): String? {
        return courses.value?.getOrNull(coursePosition)?.games?.getOrNull(gameIndex)?.let { game ->
            "${game.name} - ${game.dateTime}"
        }
    }

    fun fetchCourses() {
        // þarf að bæta við aðferð til að sækja gögn úr bakenda
        val mockCourses = listOf(
            Course("Course 1", "5 km away", listOf(
                Game("Game 1", "24 Mar, 2:00 PM", 10, "Alice"),
                Game("Game 2", "25 Mar, 3:00 PM", 8, "Bob")
            )),
            Course("Course 2", "10 km away", listOf(
                Game("Game 3", "26 Mar, 4:00 PM", 12, "Charlie"),
                Game("Game 4", "27 Mar, 5:00 PM", 7, "Dana")
            ))
        )

        _courses.postValue(mockCourses)
    }
}
