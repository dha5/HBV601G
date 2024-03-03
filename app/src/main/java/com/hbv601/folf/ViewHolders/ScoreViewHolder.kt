package com.hbv601.folf.ViewHolders

import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.databinding.ScoreItemBinding
import kotlin.math.abs

class ScoreViewHolder(private val binding: ScoreItemBinding): RecyclerView.ViewHolder(binding.root){
    fun onBind(gameTitle:String,dateTime:String,score:Int,par:Int){
        binding.gameTitle.text =gameTitle
        binding.dateTime1.text = dateTime
        if(score==par){
            binding.score.text = "Stig: ${score} = Par"
        }else if(score>par){
            binding.score.text = "Stig: ${score} = ${score-par} yfir pari"
        }else{
            binding.score.text = "Stig: ${score} = ${abs(score-par)} undir Pari"
        }

    }
}