package com.hbv601.folf

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.Entities.GameEntity
import com.hbv601.folf.databinding.FragmentYourGamesBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val RECIEVE_GAMEARRAY = "com.hbv601.folf.services.extra.RECIEVEGAMEARRAY"
private const val GAME_PARCEL_ARRAY = "com.hbv601.folf.services.extra.GAME_PARCEL_ARRAY"
 */

/**
 * A simple [Fragment] subclass.
 * Use the [YourGamesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class YourGamesFragment : Fragment() {

    private var _binding: FragmentYourGamesBinding? = null
    private val binding get() = _binding!!

    private val gameDataGames = mutableListOf<GameData>()
    private val gameEntityGames = mutableListOf<GameEntity>()




    /*private val bReceiver: BroadcastReceiver = object : BroadcastReceiver() {
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
    var bManager: LocalBroadcastManager? = null*/


    //private val calendar = Calendar.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourGamesBinding.inflate(inflater, container, false)
        binding.YourCreatedGames.removeAllViews()
        binding.GamesList.removeAllViews()


        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_your_games, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch { getGameDataGames() }

    }

    private suspend fun getGameDataGames(){
        val accessToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
        if (accessToken != null) {
            val responceGameDataGames = FolfApi.retrofitService.getLoggedInUserGames("Bearer ${accessToken}")
            Log.d("GameDataGames", responceGameDataGames.toString())
            if (responceGameDataGames.isSuccessful){
                val responceGameData = responceGameDataGames.body()
                if (responceGameData != null) {
                    for (gameData in responceGameData){
                        gameDataGames.add(gameData)
                    }
                }
            }else{
                Toast.makeText(requireContext(),
                    "Could not find any games", Toast.LENGTH_SHORT).show()
                return
            }
        }else{
            Toast.makeText(requireContext(),
                "Warning! User might not be logged in", Toast.LENGTH_SHORT).show()
            return
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}