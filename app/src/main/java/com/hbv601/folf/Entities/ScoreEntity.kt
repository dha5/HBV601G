package com.hbv601.folf.Entities

import android.os.Parcel
import android.os.Parcelable

class ScoreParcel(val gameId:Int, val players: List<String>?, val score:List<String>?):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createStringArrayList(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(gameId)
        parcel.writeStringList(players)
        parcel.writeStringList(score)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ScoreParcel> {
        override fun createFromParcel(parcel: Parcel): ScoreParcel {
            return ScoreParcel(parcel)
        }

        override fun newArray(size: Int): Array<ScoreParcel?> {
            return arrayOfNulls(size)
        }
    }

}

class ScoreEntity(val gameId:Int, val course:CourseEntity?, val players: ArrayList<String>) {
    val score:HashMap<String,ArrayList<Int>>
    init {
        val hashMap = HashMap<String,ArrayList<Int>>()
        for(player in players){
            hashMap.put(player,ArrayList<Int>())
        }
        score = hashMap
    }
    fun addPlayers(players:List<String>){
        for(player in players){
            this.players.add(player)
            this.score[player] = ArrayList<Int>()
        }
    }
    fun ScoreEntityToParcel():ScoreParcel{
        val playerList = players.toList()
        val scores = ArrayList<String>()
        for(player in playerList){
           var vScore = ""
           for(iScore in score[player]!!){
               vScore += "$iScore, "
           }
            scores.add(vScore)
        }
        return ScoreParcel(gameId,playerList,scores)
    }
    fun removePlayer(player:String){
        if(players.contains(player)){
            score.remove(player)
            players.remove(player)
        }
    }
    fun getPlayers():ArrayList<String>{
        return players
    }
    fun getPlayerScore(player:String):ArrayList<Int>{
        if(player in players) {
            return score[player]!!
        }
        return ArrayList()
    }
    fun addScore(player:String,score:Int){
        if(players.contains(player)){
            this.score[player]!!.add(score)
        }
    }
    fun editScore(player:String,index:Int,score:Int){
        if(player.contains(player)){
            val ind = players.indexOf(player)
            if(this.score[player]!!.size > index){
                this.score[player]!![index] = score
            }
        }
    }
    fun deleteScore(player:String,index:Int){
        if(player.contains(player)){
            if(score[player]!!.size>index){
                score[player]!!.removeAt(index)
            }
        }
    }


    fun addScores(players:List<String>, scores:List<Int>){
        if(players.size ==scores.size){
            var i = 0
            while(i<scores.size){
                if(players[i] in this.players){
                    this.score[players[i]]!!.add(scores[i])
                }
            }
        }
    }
    fun getPar():String{
        if(course!=null){
            //implement me
            return "not implemented yet"
        }
        return "No course present"
    }

}