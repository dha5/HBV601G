package com.hbv601.folf
import android.app.Activity
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.Entities.CourseEntity
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.Entities.PlayerEntity
import com.hbv601.folf.Entities.PostGameData
import com.hbv601.folf.Entities.UserEntity
import com.hbv601.folf.databinding.FragmentCreateGameBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch
import java.time.LocalDate


class CreateGameFragment : Fragment(), AdapterView.OnItemSelectedListener{
    //intent service deprecated, rethink
    /*private val REGISTER_GAME = "com.hbv.folf.services.action.REGISTER_GAME"
    private val UPDATE_GAME = "com.hbv.folf.services.action.UPDATE_GAME"
    private val FETCH_GAME = "com.hbv.folf.services.action.FETCH_GAME"
    private val ADD_PLAYER = "com.hbv.folf.services.action.ADD_PLAYER"
    private val GAME_PARCEL = "com.hbv601.folf.services.extra.GAME_PARCEL"
    private val RECIEVE_GAMEPARCEL = "com.hbv601.folf.RegisterFragment.GAMEPARCELRECIEVE"*/
    private var gameId:Number? = null
    private var existingGame: GameData? = null
    private var selectedCourse:String? = null
    private var selectedCourseId:Int? = null
    private var selectedPlayer:UserEntity? = null
    private var courseIds:ArrayList<Int>? = null
    private var friendsList: ArrayList<String>? = null
    private var friends: ArrayList<UserEntity>? = null
    private val calendar = Calendar.getInstance()
    private var _binding: FragmentCreateGameBinding? = null
    private val binding get() = _binding!!
    private val playerNamesList = ArrayList<String>()
    private val playerList = ArrayList<PlayerEntity>()
    private val storedCourses: MutableList<CourseEntity> = mutableListOf()

    /*private val bReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == RECIEVE_GAMEPARCEL) {
                val gameParcel = intent.getParcelableExtra(GAME_PARCEL,
                    GameParcel::class.java
                )
                if (gameParcel != null) {
                    Log.d("game created", gameParcel.gameTitle.toString())
                    Log.d("gameId", gameParcel.gameId.toString())
                    gameId = gameParcel.gameId
                    Log.d("Succesfull registeringPlayer", gameParcel.creatingPlayer.toString())
                    if(playerNamesList.isNotEmpty()){
                        val players = ArrayList<String>()
                        for(playerName in playerNamesList){
                            players.add(playerName)
                        }
                        GameService.startActionAddPlayers(context, gameId as Int,players)
                    }
                    updateButton()

                }

                println("ParcelRecieved")

                //Do something with the string
            }
        }
    }
    var bManager: LocalBroadcastManager? = null*/


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /*bManager = this.getContext()?.let { LocalBroadcastManager.getInstance(it) }
        val intentFilter = IntentFilter()
        intentFilter.addAction(RECIEVE_GAMEPARCEL)
        bManager!!.registerReceiver(bReceiver, intentFilter)*/
        _binding = FragmentCreateGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.startGameButton.visibility = View.INVISIBLE
        binding.playerLayout.visibility = View.INVISIBLE

        val gameParcel = arguments?.getParcelable("GAME_PARCEL",GameParcel::class.java)
        if(gameParcel != null){
            getGame(gameParcel.gameId.toLong())
            //binding.locationField.setText(gameParcel.course)
        }else {
            // Top layer
            binding.textViewCreateGame.text = "Create a new game"
        }
        // Second layer
        binding.gameNowButton.setOnClickListener {
            // Set current date and time to timeField
            binding.timeField.setText(LocalDate.now().toString())
            Log.d("Localdate",LocalDate.now().toString())
        }

        // Third layer
        val adapter = ArrayAdapter<String>(this@CreateGameFragment.requireContext(),android.R.layout.simple_list_item_1,playerNamesList)
        binding.playerListView.adapter = adapter
        binding.addPlayerButton.setOnClickListener {
            addPlayerFromTextfield()
        }
        getFriends()
        binding.addPlayerSpinnerButton.setOnClickListener {
            addPlayerFromSpinner()
        }
        getCourses()

        // Fourth layer
        if(existingGame==null){
            binding.registerGameButton.setOnClickListener {
                createGame()
            }
        }else{
            extantGame()
        }

        binding.startGameButton.setOnClickListener {
            if (playerNamesList.isNotEmpty()) {
                startGame()
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
    fun extantGame(){
        binding.timeField.setText(existingGame!!.datetime.toString())
        binding.titleField.setText(existingGame!!.name.toString())
        binding.playerLayout.visibility = View.VISIBLE
        binding.startGameButton.visibility = View.VISIBLE
        getPlayers(existingGame!!.id!!)
        binding.registerGameButton.setOnClickListener {
            updateGame()
        }
        binding.registerGameButton.text = "Update"
    }
    fun createGame(){
        lifecycleScope.launch{
            if(selectedCourseId==null){
                Toast.makeText(this@CreateGameFragment.requireContext(),"vinsamlegast skráið völl fyrir leik",Toast.LENGTH_SHORT).show()
                return@launch
            }
            val newGame = PostGameData(binding.titleField.text.toString(),selectedCourseId!!.toLong(),null,ArrayList<Long>())
            Log.d("createGame",newGame.toString())
            val bearerToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
            if(bearerToken!=null){
                val res = FolfApi.retrofitService.createGame("Bearer ${bearerToken}",newGame)
                Log.d("creategame",res.toString())
                if(res.isSuccessful){
                    existingGame = res.body()!!
                    extantGame()
                }else{
                    Toast.makeText(this@CreateGameFragment.requireContext(),"Ekki tókst að skapa þennan leik",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@CreateGameFragment.requireContext(),"Ekkert login til staðar, vinsamlegast skráðu þig inn",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun updateGame(){
        lifecycleScope.launch{
            val thisGame = existingGame
            val updateGame = GameData(thisGame!!.id,thisGame.creator,binding.timeField.text.toString(),binding.titleField.text.toString(),selectedCourseId)
            val res = FolfApi.retrofitService.updateGame(thisGame!!.id!!,updateGame)
            if(res.isSuccessful){
                Toast.makeText(this@CreateGameFragment.requireContext(),"Tókst að uppfæra skráningu leiks",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@CreateGameFragment.requireContext(),"Ekki tókst að uppfæra skráningu leiks",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getPlayers(gameId:Long){
        lifecycleScope.launch{
            val res = FolfApi.retrofitService.getGamePlayers(gameId)
            if(res.isSuccessful){
                for(player in res.body()!!){
                    if(!playerList.contains(player)){
                        playerNamesList.add(player.name)
                        playerList.add(player)
                    }
                }
            }else{
                Toast.makeText(this@CreateGameFragment.requireContext(),"Ekki tókst að sækja spilara fyrir þennan leik",Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun getFriends(){
        val spinner = binding.playerSpinner
        lifecycleScope.launch {
            Log.d("accesstoken",requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)!!)
            val bearerToken = "Bearer ${requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)!!}"
            val res = FolfApi.retrofitService.getFriends(bearerToken)
            Log.d("getFriends",res.toString())
            if(res.isSuccessful && res.body() != null){
                Log.d("friends","it works")
                val friendarray = res.body()!!
                friendsList = ArrayList<String>()
                friends = ArrayList<UserEntity>()
                for(friend in friendarray){
                    friendsList!!.add(friend.username)
                    friends!!.add(UserEntity(friend.id,friend.name,friend.username))
                }
                val arrayAdapter = ArrayAdapter<String>(this@CreateGameFragment.requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,friendsList!!)
                spinner.adapter = arrayAdapter
                spinner.onItemSelectedListener = this@CreateGameFragment
            }
        }
    }
    fun getGame(gameId:Long){
        lifecycleScope.launch {
            val res = FolfApi.retrofitService.getGameById(gameId)
            if(res.isSuccessful){
                existingGame = res.body()!!
                extantGame()
            }else{
                Toast.makeText(this@CreateGameFragment.requireContext(),"GameId skilaði ekki leik",Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun startGame(){
        if(playerNamesList.size>0){
            val args = Bundle().apply {
                if(existingGame != null){
                    putInt("gameId", Math.toIntExact(existingGame!!.id!!) )
                }
                putStringArray("playerNames", playerNamesList.toTypedArray())
            }
            // Navigate to InputScoreFragment with arguments
            findNavController().navigate(R.id.action_CreateGameFragment_to_InputScoreFragment, args)
        } else {
        // Handle case where no players are added
        Toast.makeText(this@CreateGameFragment.requireContext(), "Please add at least one player", Toast.LENGTH_SHORT).show()
    }
        //útfæra
    }
    fun addPlayerFromSpinner(){
        lifecycleScope.launch{
            if(selectedPlayer!=null){
                if(!playerNamesList.contains(selectedPlayer!!.username)){
                    val bearerToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
                    if(bearerToken!= null){
                        val newPlayer = PlayerEntity(null,selectedPlayer!!.id,selectedPlayer!!.username,existingGame!!.id!!)
                        Log.d("addPlayerFromSpinner",newPlayer.toString())
                        val res = FolfApi.retrofitService.addPlayer("Bearer ${bearerToken}",newPlayer)
                        Log.d("addPlayerFromSpinner",res.toString())
                        if(res.isSuccessful){
                            Toast.makeText(this@CreateGameFragment.requireContext(),"${newPlayer.name} bætt við leik",Toast.LENGTH_SHORT).show()
                            playerNamesList.add(newPlayer.name)
                            playerList.add(res.body()!!)
                        }
                    }else{
                        Toast.makeText(this@CreateGameFragment.requireContext(),"Ekkert login til staðar",Toast.LENGTH_SHORT).show()
                    }
                }else{
                    Toast.makeText(this@CreateGameFragment.requireContext(),"Spilari með þessu nafni þegar skráður á þennan leik",Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this@CreateGameFragment.requireContext(),"Vinsamlegast veldu ákveðin notanda til að bæta við leik",Toast.LENGTH_SHORT).show()
            }

        }
    }
    fun addPlayerFromTextfield(){
        lifecycleScope.launch{
            val prefs = requireActivity().getSharedPreferences("USER",0)
            val bearerToken = prefs.getString("AccessToken",null)!!
            val userId = prefs.getInt("UserId",-1).toLong()
            val name = binding.playerField.text.toString()
            val player = PlayerEntity(null,userId,name,existingGame!!.id!!)
            val res = FolfApi.retrofitService.addPlayer("Bearer ${bearerToken}",player)
            Log.d("addPlayerFromTextField",res.toString())
            if(!res.isSuccessful){
                Toast.makeText(this@CreateGameFragment.requireContext(),"ekki tókst að bæta þessu nafni við lista spilara",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this@CreateGameFragment.requireContext(),"Spilara bætt við leik",Toast.LENGTH_SHORT).show()
                playerNamesList.add(name)
                playerList.add(res.body()!!)
                binding.playerField.text.clear()
            }
        }
    }
    fun getCourses(){
        val spinner = binding.locationField
        lifecycleScope.launch {
            val courses = FolfApi.retrofitService.getFields()
            val names = ArrayList<String>()
            courseIds = ArrayList<Int>()
            if(courses.isSuccessful && courses.body()!=null) {
                for (course in courses.body()!!) {
                    val thisCourse = CourseEntity(course.name,course.location, course.description,course.id)
                    storedCourses.add(thisCourse)
                    val distanceInMeters = thisCourse.getDistanceFrom(context as Activity)
                    val distanceInKilometers = distanceInMeters.toDouble() / 1000
                    val formattedDistance = String.format("%.1f", distanceInKilometers)
                    val nafnid = course.name
                    Log.d("formattedDistance",formattedDistance)
                    val stringToAdd = "$nafnid  $formattedDistance km"

                    names.add(stringToAdd)
                    courseIds!!.add(course.id)
                }
            }
            val arrayAdapter = ArrayAdapter<String>(this@CreateGameFragment.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,names)
            spinner.adapter = arrayAdapter
            spinner.onItemSelectedListener = this@CreateGameFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
            when(parent.id) {
                R.id.playerSpinner -> {
                    selectedPlayer = friends!![position]
                }

                R.id.locationField -> {
                    selectedCourse = parent.getItemAtPosition(position).toString()
                    selectedCourseId = courseIds!![position]
                }


            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        if(parent != null){
            when(parent.id){
                R.id.playerSpinner ->{
                    selectedPlayer = null
                }
            }
        }
    }
}