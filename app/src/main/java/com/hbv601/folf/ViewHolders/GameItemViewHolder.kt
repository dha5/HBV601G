package com.hbv601.folf.ViewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.Entities.GameEntity
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.databinding.GameItemBinding

class GameItemViewHolder (private val binding:GameItemBinding): RecyclerView.ViewHolder(binding.root){

    lateinit var gluedGame : GameEntity
    lateinit var gluedGameData : GameData
    fun bindItem(game: GameEntity){
        gluedGame = game
        binding.Course.text = game.course
        binding.DateTime.text = game.time.toString()
        binding.Creator.text = game.creatingPlayer
    }
    fun bindGameClass(game: GameData){
        gluedGameData = game
        binding.Course.text = game.name
        binding.DateTime.text = game.date_created
        binding.Creator.text = game.creator.toString()
    }

    fun getGameEntity() : GameEntity{
        return gluedGame
    }
    fun getGameData() : GameData{
        return gluedGameData
    }
    fun onClick(){
            binding.ViewGameButton.visibility = View.VISIBLE
    }

    fun bindButton(game: GameParcel, listener: View.OnClickListener) {
        binding.viewStatisticsButton.visibility = View.VISIBLE
        binding.viewStatisticsButton.setOnClickListener(listener)
    }

}