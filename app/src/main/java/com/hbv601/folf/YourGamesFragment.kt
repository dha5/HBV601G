package com.hbv601.folf

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.Entities.CourseData
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.Entities.GameEntity
import com.hbv601.folf.ViewHolders.GameItemViewHolder
import com.hbv601.folf.databinding.FragmentYourGamesBinding
import com.hbv601.folf.databinding.GameItemBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch
import java.time.LocalDate

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
    private val gameDataOngoingGames = mutableListOf<GameData>()
    private val gameEntityOngoingGames = mutableListOf<GameEntity>()
    private val gameDataUpcomingGames = mutableListOf<GameData>()
    private val gameEntityUpcomingGames = mutableListOf<GameEntity>()
    private val gameDataPastGames = mutableListOf<GameData>()
    private val gameEntityPastGames = mutableListOf<GameEntity>()
    private val gameDataCreatorGames = mutableListOf<GameData>()
    private val gameEntityCreatorGames = mutableListOf<GameEntity>()






    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentYourGamesBinding.inflate(inflater, container, false)
        binding.PastGamesList.removeAllViews()
        binding.GamesList.removeAllViews()


        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_your_games, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
            lifecycleScope.launch {
                if (gameDataGames.isEmpty() || gameEntityGames.isEmpty()) { // til að vera ekki alltaf að byðja um upplýsingar
                    getOngoingGames()
                    getPastGames()
                    getUpcomingGames()
                    //getGameDataGames()
                    getGameEntity()
                }
                displayGameLists()
            }
            Log.d("eftir lifecycleScope", gameDataGames.toString())


    }

    private fun displayGameLists(){
        //all games that have the same creator id as the user id of logged in user
        for(game in gameEntityCreatorGames){
            val gameItem = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
            gameItem.bindItem(game)
            val btnEditGame = Button(requireContext())
            btnEditGame.text = "Edit Game"
            val editClickListener = View.OnClickListener{
                val args = Bundle().apply {
                    putInt("GAME_ID",game.getId())
                }
                findNavController().navigate(R.id.action_yourGames_to_createGameFragment,args)
            }
            val btnStartGame = Button(requireContext())
            btnStartGame.text = "Start"
            val startClickListener = View.OnClickListener {
                lifecycleScope.launch{
                    val res = FolfApi.retrofitService.startGame(game.getId())
                    if(res.isSuccessful){
                        val args = Bundle().apply {
                            putInt("GAME_ID",game.getId())
                        }
                        findNavController().navigate(R.id.action_yourGames_to_inputScorefragment,args)
                    }
                }

            }
            gameItem.bindButtonToBar(btnStartGame,startClickListener)
            gameItem.bindButtonToBar(btnEditGame,editClickListener)
            binding.creatorGames.addView(gameItem.itemView)
        }
        //leikir sem eru í gangi skulu hafa play takka
        for(game in gameEntityOngoingGames){
            val gameItem = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
            gameItem.bindItem(game)
            val btnPlayGame = Button(requireContext())
            btnPlayGame.text = "Play"
            val playClickListener=View.OnClickListener{
                val args = Bundle().apply {
                    putInt("GAME_ID",game.getId())
                }
                findNavController().navigate(R.id.action_yourGames_to_inputScorefragment,args)
            }
            gameItem.bindButtonToBar(btnPlayGame,playClickListener)
            binding.ongoingGames.addView(gameItem.itemView)

        }
        for(game in gameEntityUpcomingGames){
            val gameItem = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
            gameItem.bindItem(game)
            binding.upcomingGames.addView(gameItem.itemView)
        }
        for(game in gameEntityPastGames){
            val gameItem = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
            gameItem.bindItem(game)
            val btnViewStatistics = Button(requireContext())
            btnViewStatistics.text = "View Statistics"
            val statisticsClickListener = View.OnClickListener {
                val args = Bundle().apply {
                    putInt("GAME_ID",game.getId())
                    putInt("FIELD_ID",game.fieldId)
                }
                findNavController().navigate(
                    R.id.action_YourGames_to_StatisticsFragment,
                    args
                )
            }
            gameItem.bindButtonToBar(btnViewStatistics,statisticsClickListener)
            binding.PastGamesList.addView(gameItem.itemView)
        }

        /*for (game in gameEntityGames) {
            val playDate = game.time
            Log.d("Game in gameEntityGames", game.toString())
            val gameItem = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
            gameItem.bindItem(game)
            if (playDate > LocalDate.now().minusDays(10)) {
                    binding.GamesList.addView(gameItem.itemView)
                }else{
                    binding.PastGamesList.addView(gameItem.itemView)
                }


                val btnViewStatistics = Button(requireContext())
                btnViewStatistics.text = "View Statistics"
                val statisticsClickListener = View.OnClickListener {
                    val args = Bundle().apply {
                        putParcelable("GAME_PARCEL", game.toGameParcel())
                    }
                    findNavController().navigate(
                        R.id.action_YourGames_to_StatisticsFragment,
                        args
                    )
                }

                gameItem.bindButton(game.toGameParcel(), statisticsClickListener)
                //btnViewStatistics.setOnClickListener(statisticsClickListener)
                //binding.GamesList.addView(btnViewStatistics)


        }*/
    }
    /*private suspend fun getGameDataGames(){
        gameDataGames.clear()
        val bearerToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
        if (bearerToken != null) {
            Log.d("fyrir getloggedinUser","")
            val responceGameDataGames = FolfApi.retrofitService.getLoggedInUserGames("Bearer ${bearerToken}")

            if (responceGameDataGames.isSuccessful){
                Log.d("Before asking for body", responceGameDataGames.toString())
                val responceGameData = responceGameDataGames.body()
                Log.d("GameDataGames", responceGameData.toString())
                if (responceGameData != null) {
                    for (gameData in responceGameData){
                        gameDataGames.add(gameData)
                    }
                    Log.d("eftir foor loop", gameDataGames.toString())
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

    }*/
    private suspend fun getUpcomingGames(){
        val bearerToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
        val userId = requireActivity().getSharedPreferences("USER",0).getInt("UserId",-1)
        if (bearerToken != null){
            val res = FolfApi.retrofitService.getYourUpcomingGames("Bearer $bearerToken")
            if(res.isSuccessful){
                val games = res.body()
                if(games!=null){
                    for(game in games){
                            if (game.creator ==userId){
                                gameDataCreatorGames.add(game)
                            }else{
                                gameDataUpcomingGames.add(game)
                            }
                    }
                }
            }
        }
    }
    private suspend fun getPastGames(){
        val bearerToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
        if(bearerToken!=null){
            val res = FolfApi.retrofitService.getYourPastGames("Bearer $bearerToken")
            Log.d("pastGames",res.toString())
            if(res.isSuccessful){
                val games = res.body()
                Log.d("pastGames",games.toString())
                if(games!=null){
                    for(game in games){
                        gameDataPastGames.add(game)
                    }
                }
            }
        }
    }
    private suspend fun getOngoingGames(){
        val bearerToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
        if(bearerToken!=null){
            val res = FolfApi.retrofitService.getYourOngoingGames("Bearer $bearerToken")
            if(res.isSuccessful){
                val games = res.body()
                if(games!=null){
                    for(game in games){
                        gameDataOngoingGames.add(game)
                    }
                }
            }
        }
    }
    private fun mapGameDataToEntity(gameData: GameData,fields:List<CourseData>,name:String):GameEntity{
        var fieldname = "ErrorField"
        for(field in fields){
            if(field.id.equals(gameData.field_id)){
                fieldname = field.name
                break
            }
        }
        var gameDate : LocalDate
        try {
            gameDate = LocalDate.parse(gameData.date_created)
        }catch (e: java.time.DateTimeException){
            Log.e("LocalDateError",e.toString())
            gameDate = LocalDate.parse("1996-01-20") //Þurfti að setja eithvað. afmælið mitt virkar :D

        }

        var tempFieldId = 0
        if (gameData.field_id != null){
            tempFieldId = gameData.field_id
        }
        val gameEntity = GameEntity(gameData.name, fieldname, gameDate, name, tempFieldId )
        gameEntity.setId(gameData.id!!.toInt())
        return gameEntity
    }
    private suspend fun getGameEntity(){
        gameEntityGames.clear()
        gameEntityOngoingGames.clear()
        gameEntityUpcomingGames.clear()
        gameEntityPastGames.clear()
        gameEntityCreatorGames.clear()
        val bearerToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
        if (bearerToken != null) {
            var hopefullPlayerName = requireActivity().getSharedPreferences("USER", 0).getString("Username", null)
            if (hopefullPlayerName == null){hopefullPlayerName = "noName"}
            var fields: List<CourseData>? = null
            val responceFields = FolfApi.retrofitService.getFields()
            if (responceFields.isSuccessful){
                 fields = responceFields.body()
            }
            if(fields == null) fields = listOf()
            for(gameData in gameDataOngoingGames){
                gameEntityOngoingGames.add(mapGameDataToEntity(gameData,fields,hopefullPlayerName))
            }
            for(gameData in gameDataCreatorGames){
                gameEntityCreatorGames.add(mapGameDataToEntity(gameData,fields,hopefullPlayerName))
            }
            for(gameData in gameDataUpcomingGames){
                gameEntityUpcomingGames.add(mapGameDataToEntity(gameData,fields,hopefullPlayerName))
            }
            for(gameData in gameDataPastGames){
                gameEntityPastGames.add(mapGameDataToEntity(gameData,fields,hopefullPlayerName))
            }
            /*
            for(gameData in gameDataGames){
                var dummyFieldName = "dummyfield"
                if (fields != null){
                    for (field in fields){
                        if (field.id.equals(gameData.field_id)){
                            dummyFieldName = field.name
                        }
                    }
                }
                var gameDate : LocalDate
                try {
                     gameDate = LocalDate.parse(gameData.date_created)
                }catch (e: java.time.DateTimeException){
                    Log.e("LocalDateError",e.toString())
                    gameDate = LocalDate.parse("1996-01-20") //Þurfti að setja eithvað. afmælið mitt virkar :D

                }

                var tempFieldId = 0
                if (gameData.field_id != null){
                     tempFieldId = gameData.field_id
                }
                val game = GameEntity(gameData.name, dummyFieldName, gameDate, hopefullPlayerName, tempFieldId )
                gameEntityGames.add(game)
            }*/
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}