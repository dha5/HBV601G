package com.hbv601.folf.ViewHolders

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.CourseData
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.databinding.CourseItemBinding
import com.hbv601.folf.databinding.GameItemBinding
import com.hbv601.folf.network.FolfApi


class CourseViewHolder (private val binding: CourseItemBinding): RecyclerView.ViewHolder(binding.root) {
    suspend fun bindItem(course: CourseData, context: Context){
        binding.CourseName.text = course.name
        val resGames = FolfApi.retrofitService.getGamesByFieldId(course.id)
        if (resGames.isSuccessful) {
            val games = resGames.body()
            val adapter = games?.let { GamesAdapter(it) }
            binding.gameslist.layoutManager = LinearLayoutManager(context)
            binding.gameslist.adapter = adapter
            binding.bestScoreTitle.visibility = View.INVISIBLE
        }
    }
    fun bestScore(view:View){
        binding.bestScore.removeAllViews()
        binding.bestScore.addView(view)
        binding.bestScoreTitle.visibility = View.VISIBLE
    }

}

private class GamesAdapter(private val gamesList:List<GameData>): RecyclerView.Adapter<GameItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = GameItemBinding.inflate(inflater)
        return GameItemViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    override fun onBindViewHolder(holder: GameItemViewHolder, position: Int) {
        val currentGame = gamesList[position]
        holder.bindGameClass(currentGame)
    }

}