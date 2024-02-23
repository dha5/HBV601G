package com.hbv601.folf.Entities

import java.util.Date

class GameEntity(var gameTitle:String, var course:String, var time:Date, var creatingPlayer:String) {
    lateinit var players:ArrayList<String>

    private var gameId: Int = -1
    fun setId(gameId: Int): Int {
        if(this.gameId == -1){
            this.gameId = gameId
        }
        return this.gameId;
    }
    fun updateGame(gameTitle: String, course:String,time:Date){
        this.gameTitle = gameTitle
        this.course = course
        this.time = time
    }
    fun addPlayer(player:String): Boolean {
        if(player in players){
            return true
        }else{
            this.players.add(player)
            if(player in players){
                return true
            }
        }
        return false
    }
    fun removePlayer(player: String):Boolean{
        if(player in players){
            players.remove(player)
            return player !in players
        }
        return true
    }
    fun updateTime(time: Date){
        this.time = time
    }
    fun updateTitle(title:String){
        this.gameTitle = title
    }

    fun updateCourse(course: String) {
        this.course = course
    }
}