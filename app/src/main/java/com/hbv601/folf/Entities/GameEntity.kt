package com.hbv601.folf.Entities

import java.util.Date

class GameEntity(var gameTitle:String, var course:String, var time:Date, var creatingPlayer:String) {
    lateinit var players:ArrayList<String>

    private var gameId: Number = 0
    fun setId(gameId: Number): Number {
        if(this.gameId == 0){
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
}