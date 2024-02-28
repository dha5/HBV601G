package com.hbv601.folf.ViewHolders

import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.databinding.GameItemBinding

class GameItemViewHolder (private val binding:GameItemBinding): RecyclerView.ViewHolder(binding.root){

    fun bindItem(game: GameParcel){
        binding.Course.text = game.course
        binding.DateTime.text = game.time
        binding.Creator.text = game.creatingPlayer
    }
}