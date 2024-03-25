package com.hbv601.folf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.databinding.FragmentInputScoreBinding

class InputScoreFragment : Fragment() {
    private lateinit var binding: FragmentInputScoreBinding
    private lateinit var playerNames: Array<String>
    private val playerScores: MutableMap<String, Int> = mutableMapOf()

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
        playerNames = arguments?.getStringArray("playerNames") ?: emptyArray()

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
        }
        binding.buttonFinishGame.setOnClickListener {
            findNavController().navigate(R.id.action_InputScoreFragment_to_LeaderboardFragment)
        }


    }


}
