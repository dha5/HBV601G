package com.hbv601.folf.Entities

import java.util.Date

class GameEntity(course:String,time:Date,players:List<String>) {
    private var gameId: Number = 0
    fun setId(gameId: Number): Number {
        if(this.gameId == 0){
            this.gameId = gameId
        }
        return this.gameId;
    }
    fun
}