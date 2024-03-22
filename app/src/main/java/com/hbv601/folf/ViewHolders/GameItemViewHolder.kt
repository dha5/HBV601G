package com.hbv601.folf.ViewHolders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.Game
import com.hbv601.folf.databinding.GameItemBinding

class GameItemViewHolder (private val binding:GameItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bindItem(game: GameParcel){
        binding.Course.text = game.course
        binding.DateTime.text = game.time
        binding.Creator.text = game.creatingPlayer
    }
    fun bindGameClass(game: Game){
        binding.Course.text = game.name
        binding.DateTime.text = game.dateTime
        binding.Creator.text = game.creatorName
    }
    fun onClick(){
            binding.ViewGameButton.visibility = View.VISIBLE
    }

    fun bindButton(game: GameParcel, listener: View.OnClickListener) {
        binding.viewStatisticsButton.visibility = View.VISIBLE
        binding.viewStatisticsButton.setOnClickListener(listener)
    }

}