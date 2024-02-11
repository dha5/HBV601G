package com.hbv601.folf
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.databinding.FragmentCreateGameBinding

class CreateGameFragment : Fragment() {

    private var _binding: FragmentCreateGameBinding? = null
    private val binding get() = _binding!!
    private val playerNamesList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Top layer
        binding.textViewCreateGame.text = "Create a new game"

        // Second layer
        binding.gameNowButton.setOnClickListener {
            // Set current date and time to timeField
            binding.timeField.setText(java.time.LocalDateTime.now().toString())
        }

        // Third layer
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, playerNamesList)
        binding.playerListView.adapter = adapter

        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerField.text.toString().trim()
            if (playerName.isNotBlank()) {
                playerNamesList.add(playerName)
                adapter.notifyDataSetChanged()
                binding.playerField.text.clear()
            }
        }

        // Fourth layer
        binding.registerGameButton.setOnClickListener {
            // Add action for Register Game button
        }

        binding.startGameButton.setOnClickListener {
            if (playerNamesList.isNotEmpty()) {
                // Create bundle with playerNamesList
                val args = Bundle().apply {
                    putStringArray("playerNames", playerNamesList.toTypedArray())
                }
                // Navigate to InputScoreFragment with arguments
                findNavController().navigate(R.id.action_CreateGameFragment_to_InputScoreFragment, args)
            } else {
                // Handle case where no players are added
                Toast.makeText(requireContext(), "Please add at least one player", Toast.LENGTH_SHORT).show()
            }
        }


        binding.cancelButton.setOnClickListener {
            // Handle cancel button action, for example:
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}