package com.hbv601.folf.ViewHolders

import android.content.Context
import android.util.Log
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

        val bearerToken = context.getSharedPreferences("USER", 0).getString("AccessToken", null)
        Log.d("AccessToken", bearerToken ?: "Token is null or empty")

        val resGamesByFieldId = FolfApi.retrofitService.getGamesByFieldId(course.id)
        val resUserGames = FolfApi.retrofitService.getLoggedInUserGames("Bearer ${bearerToken}")

        if (!resGamesByFieldId.isSuccessful) {
            Log.e("CourseViewHolder", "Failed to fetch games by Field ID: ${resGamesByFieldId.message()}")
        }

        if (!resUserGames.isSuccessful) {
            Log.e("CourseViewHolder", "Failed to fetch user games: ${resUserGames.message()}")
        }


        if (resGamesByFieldId.isSuccessful && resUserGames.isSuccessful) {
            val gamesByFieldId = resGamesByFieldId.body()
            val userGames = resUserGames.body()

            gamesByFieldId?.let { fieldGames ->
                userGames?.let { allUserGames ->
                    val filteredGames = fieldGames.filter { fieldGame ->
                        allUserGames.any { userGame ->
                            userGame.id == fieldGame.id
                        }
                    }
                    val adapter = GamesAdapter(filteredGames)
                    binding.gameslist.layoutManager = LinearLayoutManager(context)
                    binding.gameslist.adapter = adapter
                    binding.bestScoreTitle.visibility = View.INVISIBLE
                }
            }
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