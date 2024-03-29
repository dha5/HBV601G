package com.hbv601.folf

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hbv601.folf.databinding.FragmentLeaderboardBinding

class LeaderboardFragment : Fragment() {
    private lateinit var binding: FragmentLeaderboardBinding
    private lateinit var playerNames: Array<String>
    private lateinit var playerScores: IntArray

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playerNames = requireArguments().getStringArray("playerNames") ?: emptyArray()
        playerScores = requireArguments().getIntArray("playerScores") ?: IntArray(playerNames.size)

        val playerNameList = playerNames.toList()
        val playerScoreList = playerScores.toList()

        val sortedPlayers = playerNameList.zip(playerScoreList).sortedBy { it.second }

        sortedPlayers.forEachIndexed { index, (name, score) ->
            val rowView = layoutInflater.inflate(R.layout.item_leaderboard_player, null)
            val playerNameTextView = rowView.findViewById<TextView>(R.id.textViewPlayerName)
            val playerScoreTextView = rowView.findViewById<TextView>(R.id.textViewPlayerScore)

            playerNameTextView.text = name
            playerScoreTextView.text = score.toString()

            binding.tableLeaderboard.addView(rowView, index)
        }
    }
}
