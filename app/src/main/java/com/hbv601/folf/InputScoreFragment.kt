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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentInputScoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve playerNames argument
        playerNames = arguments?.getStringArray("playerNames") ?: emptyArray()

        // Inflate player score rows
        playerNames.forEach { playerName ->
            val rowView = layoutInflater.inflate(R.layout.item_player_score, null)
            val playerNameTextView = rowView.findViewById<TextView>(R.id.textViewPlayerName)
            playerNameTextView.text = playerName

            // Add the player score row to the layout
            binding.playerScoresLayout.addView(rowView)
        }

        // Submit Scores Button Click Listener
        binding.buttonSubmitScores.setOnClickListener {
            // Implement your logic to handle submitting scores
        }
    }
}


