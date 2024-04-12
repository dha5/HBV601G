package com.hbv601.folf

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.Entities.CourseData
import com.hbv601.folf.Entities.HoleData
import com.hbv601.folf.Entities.PlayerEntity
import com.hbv601.folf.Entities.ScoreData
import com.hbv601.folf.ViewHolders.PlayerScoreViewHolder
import com.hbv601.folf.databinding.FragmentInputScoreBinding
import com.hbv601.folf.databinding.ItemPlayerScoreBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch

class InputScoreFragment : Fragment() {
    private lateinit var binding: FragmentInputScoreBinding
    private lateinit var playerNames: Array<String>
    private val playerScores: MutableMap<String, Int> = mutableMapOf()
    private val playerScoreItems: MutableMap<Long, MutableList<ScoreData>> = mutableMapOf()
    private val playersMap: MutableMap<Long,PlayerEntity> = mutableMapOf()
    private val holesList: MutableList<HoleData> = mutableListOf()
    private val playerScoreViewHolderList: MutableMap<Long,PlayerScoreViewHolder> = mutableMapOf()
    private lateinit var course:CourseData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gameId = arguments?.getInt("gameId")
        if (gameId != null) fetchGame(gameId.toLong());
        /*playerNames = arguments?.getStringArray("playerNames") ?: emptyArray()

        playerNames.forEach { playerName ->
            val rowView = layoutInflater.inflate(R.layout.item_player_score, null)
            val playerNameTextView = rowView.findViewById<TextView>(R.id.textViewPlayerName)
            playerNameTextView.text = playerName

            val scoreEditText = rowView.findViewById<EditText>(R.id.editTextScore)
            val totalScoreEditText = rowView.findViewById<EditText>(R.id.editTextTotalScore)

            val submitButton = rowView.findViewById<Button>(R.id.buttonSubmitScore)
            submitButton.setOnClickListener {
                val score = scoreEditText.text.toString().toIntOrNull() ?: return@setOnClickListener
                playerScores[playerName] = (playerScores[playerName] ?: 0) + score
                totalScoreEditText.setText(playerScores[playerName].toString())
            }

            binding.playerScoresLayout.addView(rowView)
        }*/
        binding.buttonFinishGame.setOnClickListener {
            findNavController().navigate(R.id.action_InputScoreFragment_to_LeaderboardFragment)
        }


    }

    fun fetchGame(gameId:Long){
        lifecycleScope.launch {
            //byrjum á því að sækja gameData til að staðfesta tilvist leiks
            val game = FolfApi.retrofitService.getGameById(gameId)
            Log.d("gameres",game.toString())
            if(game.body()!=null){
                Log.d("gameres Body",game.body().toString())
            }
            if(!game.isSuccessful){
                Toast.makeText(this@InputScoreFragment.requireContext(),"Ekki fannst leikur með gefnu auðkenni",Toast.LENGTH_SHORT).show()
                return@launch
            }
            val holes = FolfApi.retrofitService.getHolesByFieldId(game.body()!!.field_id!!)
            if(holes.isSuccessful){
                for(hole in holes.body()!!){
                    holesList.add(hole)
                }
            }
            val players = FolfApi.retrofitService.getGamePlayers(gameId)
            if(players.isSuccessful && players.body() != null){
                for(player in players.body()!!){
                    playersMap[player.id!!] = player
                }
            }
            val scores = FolfApi.retrofitService.getScoreByGameId(gameId)
            if(scores.isSuccessful && scores.body()?.size!! >0){
                for(score in scores.body()!!){
                    if(score.player_id in playerScoreItems.keys){
                        playerScoreItems[score.player_id]?.add(score)
                    }else{
                        playerScoreItems[score.player_id] = mutableListOf(score)
                    }
                }
            }
            for(player in playersMap.values){
                val playerScoreItemBinding = ItemPlayerScoreBinding.inflate(layoutInflater)
                val viewHolder = PlayerScoreViewHolder(playerScoreItemBinding)
                viewHolder.onBind(player,holesList.toList(),playerScoreItems[player.id]?.toList());
                binding.playerScoresLayout.addView(viewHolder.itemView)
                viewHolder.postScore.setOnClickListener { player.id?.let { it1 ->
                    postViewHolderScoreData(
                        it1
                    )
                } }
                playerScoreViewHolderList[player.id!!]=viewHolder

            }

        }
    }
    fun postViewHolderScoreData(player_id:Long){
        if(playerScoreViewHolderList[player_id]!=null){
        postScore(playerScoreViewHolderList[player_id]!!.currentScoreData)}
    }

    fun postScore(scoreData:ScoreData){
        lifecycleScope.launch {
            val token = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
            if(token!=null){
                var newScoreData:ScoreData? = null
                if(scoreData.id!=null){
                    val updateScore = FolfApi.retrofitService.updateScore("Bearer $token",scoreData)
                    if(updateScore.isSuccessful && updateScore.body()!=null) {
                        newScoreData = updateScore.body()!!
                    }
                }else{
                val postScore = FolfApi.retrofitService.postNewScore("Bearer $token",scoreData)
                    if(postScore.isSuccessful && postScore.body()!=null) {
                        newScoreData = postScore.body()!!
                    }
                }
                if(newScoreData!=null){
                    if(playerScoreViewHolderList[newScoreData.player_id]!=null) {
                        playerScoreViewHolderList[newScoreData.player_id]!!.addScoreData(newScoreData)
                    }
                    if(playerScoreItems[newScoreData.player_id]!=null) {
                        playerScoreItems[newScoreData.player_id]!!.add(newScoreData)
                    }else{
                        playerScoreItems[newScoreData.player_id] = mutableListOf(newScoreData)
                    }
                    Toast.makeText(this@InputScoreFragment.requireContext(),"Nýtt skor skráð fyrir spilara"+ playersMap[newScoreData.player_id]!!.name,Toast.LENGTH_SHORT).show()
                }else{
                    Toast.makeText(this@InputScoreFragment.requireContext(),"Eitthvað fór úrskeiðis með að skrá skor",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@InputScoreFragment.requireContext(),"Login token fannst ekki, vinsamlegast skráið aftur inn",Toast.LENGTH_SHORT).show()
            }
        }
    }


}
