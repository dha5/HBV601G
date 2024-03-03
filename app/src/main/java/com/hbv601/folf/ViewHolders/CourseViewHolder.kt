package com.hbv601.folf.ViewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Course
import com.hbv601.folf.Game
import com.hbv601.folf.R
import com.hbv601.folf.databinding.CourseItemBinding
import com.hbv601.folf.databinding.GameItemBinding


class CourseViewHolder (private val binding: CourseItemBinding): RecyclerView.ViewHolder(binding.root) {
    fun bindItem(course: Course){
        binding.CourseName.text = course.name
        binding.gameslist.adapter = GamesAdapter()
    }
}

private class GamesAdapter(private val gamesList:List<Game>): RecyclerView.Adapter<GameItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameItemViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(
            GameItemBinding, parent,false
        )
        return GameItemViewHolder(inflater)
    }

    override fun getItemCount(): Int {
        return gamesList.size
    }

    override fun onBindViewHolder(holder: GameItemViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

}