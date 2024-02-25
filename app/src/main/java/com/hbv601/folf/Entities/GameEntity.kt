package com.hbv601.folf.Entities

import android.os.Parcel
import android.os.Parcelable
import java.util.Date



class GameParcel(
    val gameId: Int,
    val gameTitle: String?,
    val course:String?,
    val time:String?,
    val creatingPlayer: String?, val players:List<String>?):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(gameId)
        parcel.writeString(gameTitle)
        parcel.writeString(course)
        parcel.writeString(time)
        parcel.writeString(creatingPlayer)
        parcel.writeStringList(players)
    }
    /**
    fun parcelToGameEntity(){
        val time:Date = time
        val gameEntity = creatingPlayer?.let {
            if (gameTitle != null) {
                if (course != null) {
                    GameEntity(gameTitle,course, time, it)
                }
            }
        }
    }
    **/
    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GameParcel> {

        override fun createFromParcel(parcel: Parcel): GameParcel {
            return GameParcel(parcel)
        }

        override fun newArray(size: Int): Array<GameParcel?> {
            return arrayOfNulls(size)
        }
    }
}

class GameEntity(var gameTitle:String, var course:String, var time:Date, var creatingPlayer:String) {
    lateinit var players:ArrayList<String>

    private var gameId: Int = -1
    fun setId(gameId: Int): Int {
        if(this.gameId == -1){
            this.gameId = gameId
        }
        return this.gameId;
    }
    fun getId():Int{
        return this.gameId
    }
    fun gameEntityToParcel():GameParcel{
        val timeString = time.toString();
        return GameParcel(gameId,gameTitle,course,timeString,creatingPlayer,players.toList())
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