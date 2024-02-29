package com.hbv601.folf.ViewHolders

import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.Entities.ScoreParcel
import com.hbv601.folf.databinding.PastGameItemBinding

class PastGameItemViewHolder(private val binding: PastGameItemBinding):RecyclerView.ViewHolder(binding.root) {
    fun bindItem(gameParcel: GameParcel, scoreParcel: ScoreParcel, username:String){
        binding.course.text = gameParcel.course
        binding.dateTime.text = gameParcel.time
        for((i,player) in scoreParcel.players!!.withIndex()){
            if(player == username){
                if(scoreParcel.score?.get(i)!=null){
                    binding.Score.text = scoreParcel.score[i]
                }
            }
        }
    }

}