package com.hbv601.folf

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.ViewHolders.GameItemViewHolder
import com.hbv601.folf.databinding.FragmentYourGamesBinding
import com.hbv601.folf.databinding.GameItemBinding
import com.hbv601.folf.services.GameService

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val RECIEVE_GAMEARRAY = "com.hbv601.folf.services.extra.RECIEVEGAMEARRAY"
private const val GAME_PARCEL_ARRAY = "com.hbv601.folf.services.extra.GAME_PARCEL_ARRAY"

/**
 * A simple [Fragment] subclass.
 * Use the [YourGamesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YourGamesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var username: String = "John"

    private val bReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("recieve","intent Recieved ${intent.action}")
            if (intent.action == RECIEVE_GAMEARRAY) {
                val games = intent.getParcelableArrayExtra(GAME_PARCEL_ARRAY,GameParcel::class.java)
                Log.d("recieve","gameArray recieved")
                if(games!=null){
                    Log.d("recieve", games.size.toString())
                    for((i, game) in games.withIndex()){
                        Log.d("game",game.toString())
                        val gameItem = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
                        gameItem.bindItem(game as GameParcel)
                        binding.GamesList.addView(gameItem.itemView, i)

                    }
                }

                //Do something with the string
            }
        }
    }
    var bManager: LocalBroadcastManager? = null
    private val calendar = Calendar.getInstance()
    private var _binding: FragmentYourGamesBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bManager = this.context?.let { LocalBroadcastManager.getInstance(it) }
        val intentFilter = IntentFilter()
        intentFilter.addAction(RECIEVE_GAMEARRAY)
        bManager!!.registerReceiver(bReceiver, intentFilter)
        this.context?.let { GameService.startActionFetchPlayerGames(it,username) }
        _binding = FragmentYourGamesBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_your_games, container, false)
        return binding.root
    }
/*
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment YourGamesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            YourGamesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }*/
}