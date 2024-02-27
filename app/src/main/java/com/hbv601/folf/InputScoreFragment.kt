package com.hbv601.folf
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hbv601.folf.databinding.FragmentInputScoreBinding

class InputScoreFragment : Fragment() {
    private lateinit var binding: FragmentInputScoreBinding
    private lateinit var playerNames: Array<String>
    private lateinit var scores: Array<String>
    private var gameId: Number? = null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentInputScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gameId = arguments?.getInt("gameId")
        playerNames = arguments?.getStringArray("playerNames") ?: emptyArray()

        playerNames.forEach { playerName ->
            val rowView = layoutInflater.inflate(R.layout.item_player_score, null)
            val playerNameTextView = rowView.findViewById<TextView>(R.id.textViewPlayerName)
            playerNameTextView.text = playerName

            binding.playerScoresLayout.addView(rowView)
        }

        binding.buttonSubmitScores.setOnClickListener {
        }
    }
    fun gameExists(){
        

    }
    fun updateScores(newScores: Array<Number>) {
        //add some sort of service connection
    }
    fun updateScore(newScore: Number){
        //add some sort of service connection
    }
    fun refreshScores(){
        //call to service to check updated scores
        if(scores.isNotEmpty()){


        }
    }
}