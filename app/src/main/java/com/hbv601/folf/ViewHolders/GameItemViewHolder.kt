package com.hbv601.folf.ViewHolders

import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.Entities.GameEntity
import com.hbv601.folf.databinding.GameItemBinding

class GameItemViewHolder (private val binding:GameItemBinding): RecyclerView.ViewHolder(binding.root){

    lateinit var gluedGame : GameEntity
    lateinit var gluedGameData : GameData
    fun bindItem(game: GameEntity){
        val text = "${game.course} - ${game.gameTitle} "
        gluedGame = game
        binding.Course.text = text
        binding.DateTime.text = game.dateString
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

    /*fun bindButton(game: GameParcel, listener: View.OnClickListener) {
        binding.viewStatisticsButton.visibility = View.VISIBLE
        binding.viewStatisticsButton.setOnClickListener(listener)
    }*/
    fun bindButtonToBar(button:Button,listener: View.OnClickListener){
        button.setOnClickListener(listener)
        button.layoutParams =ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        binding.buttonBar.addView(button)
    }

}