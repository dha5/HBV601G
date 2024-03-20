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
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.ViewHolders.GameItemViewHolder
import com.hbv601.folf.ViewHolders.ScoreViewHolder
import com.hbv601.folf.databinding.FragmentYourGamesBinding
import com.hbv601.folf.databinding.GameItemBinding
import com.hbv601.folf.databinding.ScoreItemBinding
import com.hbv601.folf.services.GameService
import java.time.LocalDate
import java.time.ZoneOffset

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

    private var _binding: FragmentYourGamesBinding? = null
    private val binding get() = _binding!!


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
                    for( game in games){
                        Log.d("game",game.course!!)
                        val gameItem = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
                        gameItem.bindItem(game as GameParcel)

                        //athuga hvort leikurinn sé búinn, leikurinn svo settur í "Past Games" ef dagsetning er liðin.
                        val gameTimeString = game.time //sækja game time sem streng
                        val currentTime = LocalDate.now() //sækja núverandi tíma
                        val gameTime = LocalDate.parse(gameTimeString) //breyta game time úr streng í LocalDate
                        val gameTimeMillis = gameTime.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli() //breyta úr LocalDate í milliseconds
                        val currentMillis = currentTime.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
                        if (gameTimeMillis < currentMillis) {
                            binding.PastGamesList.addView(gameItem.itemView)

                            val btnViewStatistics = Button(requireContext())
                            btnViewStatistics.text = "View Statistics"
                            val statisticsClickListener = View.OnClickListener {
                                val args = Bundle().apply {
                                    putParcelable("GAME_PARCEL", game)
                                }
                                findNavController().navigate(R.id.action_homePageFragment_to_StatisticsFragment, args)
                            }
                            btnViewStatistics.setOnClickListener(statisticsClickListener)

                            binding.PastGamesList.addView(btnViewStatistics)
                        }

                        //leikur settur í "Your Created Games" ef notandi bjó til leikinn
                        else if(game.creatingPlayer!! == username){

                            val yourGame = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
                            yourGame.bindItem(game)
                            yourGame.onClick()
                            yourGame.itemView.setOnClickListener{
                                val args = Bundle().apply {
                                    putParcelable("GAME_PARCEL",game)
                                }
                                findNavController().navigate(R.id.action_yourGamesFragment_to_CreateGameFragment,args)

                            }
                            binding.YourCreatedGames.addView(yourGame.itemView)
                        }
                        //leikur settur í "Games you have been invited to" ef notandi er ekki eigandi leiks.
                        else {
                            binding.GamesList.addView(gameItem.itemView)
                        }

                    }
                }

                //Do something with the string
            }
        }
    }
    var bManager: LocalBroadcastManager? = null
    private val calendar = Calendar.getInstance()


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
        _binding = FragmentYourGamesBinding.inflate(inflater, container, false)
        binding.YourCreatedGames.removeAllViews()
        binding.GamesList.removeAllViews()
        bManager = this.context?.let { LocalBroadcastManager.getInstance(it) }
        val intentFilter = IntentFilter()
        intentFilter.addAction(RECIEVE_GAMEARRAY)
        bManager!!.registerReceiver(bReceiver, intentFilter)
        this.context?.let { GameService.startActionFetchPlayerGames(it,username) }
        getBestScore()
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_your_games, container, false)
        return binding.root
    }
    fun getBestScore(){
        val scoreBinding = ScoreItemBinding.inflate(layoutInflater)
        val scoreViewHolder = ScoreViewHolder(scoreBinding)
        scoreViewHolder.onBind("FrolfMót HÍ-inga","20.Mars 2021",18,20)
        binding.PastGamesList.addView(scoreViewHolder.itemView)


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