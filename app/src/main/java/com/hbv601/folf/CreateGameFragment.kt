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
    private lateinit var playerNameAdapter: ArrayAdapter<String>

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
        val gameId = arguments?.getInt("GAME_ID")
        val gameParcel = arguments?.getParcelable("GAME_PARCEL",GameParcel::class.java)
        if(gameId!=null){
            getGame(gameId.toLong())
        }else if(gameParcel != null){
            getGame(gameParcel.gameId.toLong())
            //binding.locationField.setText(gameParcel.course)
        }else {
            // Top layer
            binding.textViewCreateGame.text = "Create a new game"
        }
        // Second layer
        binding.timeField.setText(LocalDate.now().toString())
        binding.gameNowButton.setOnClickListener {
            // Set current date and time to timeField
            binding.timeField.setText(LocalDate.now().toString())
            Log.d("Localdate",LocalDate.now().toString())
        }

        // Third layer
        playerNameAdapter = ArrayAdapter<String>(this@CreateGameFragment.requireContext(),android.R.layout.simple_list_item_1,playerNamesList)
        binding.playerListView.adapter = playerNameAdapter
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
            if (playerList.isNotEmpty()) {
                startGame()
            } else {
                // Handle case where no players are added
                Toast.makeText(requireContext(), "Please add at least one player", Toast.LENGTH_SHORT).show()
            }
        }
        binding.gameHomeButton.setOnClickListener {
            findNavController().navigate(R.id.action_createGame_to_home)
        }


        binding.cancelButton.setOnClickListener {
            // Handle cancel button action, for example:
            findNavController().popBackStack()
        }
    }
    fun extantGame(){
        binding.timeField.setText(existingGame!!.date_created.toString())
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
            val date = binding.timeField.text.toString().split("-")
            val day = date[0]
            val month = date[1]
            val year = date[2]
            val newGame = PostGameData(
                binding.titleField.text.toString(),
                selectedCourseId!!.toLong(),
                "${year}-${month}-${day}",
                ArrayList<Long>()
            )
            Log.d("createGame",newGame.toString())
            val bearerToken = requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)
            if(bearerToken!=null){
                val res = FolfApi.retrofitService.createGame("Bearer ${bearerToken}",newGame)
                Log.d("creategame",res.toString())
                if(res.isSuccessful){
                    existingGame = res.body()!!
                        val prefs = requireActivity().getSharedPreferences("USER",0)
                        val userId = prefs.getInt("UserId",-1).toLong()
                        val name = prefs.getString("Name",null)
                        val userName = prefs.getString("Username", "")
                        val player = if(name!=null && name.length>0){
                            PlayerEntity(null,userId,name,existingGame!!.id!!)
                        }else {
                            PlayerEntity(null,userId,userName!!,existingGame!!.id!!)
                        }
                        val res = FolfApi.retrofitService.addPlayer("Bearer $bearerToken",player)
                        if (res.isSuccessful) {
                            playerNameAdapter.notifyDataSetChanged()
                        }

                        //Þessi kóði er til að bæta logged in user í leikinn sjálfkrafa


                    Log.d("existingGame",existingGame.toString())
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
                        playerNameAdapter.notifyDataSetChanged()
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
            val status = FolfApi.retrofitService.gameStatus(gameId)
            if(status.isSuccessful){
                if(status.body() == "started"){
                    val args = Bundle().apply {
                        putInt("gameId", Math.toIntExact(existingGame!!.id!!))
                    }
                    findNavController().navigate(R.id.action_CreateGameFragment_to_InputScoreFragment,args)
                }else{
                    val res = FolfApi.retrofitService.getGameById(gameId)
                    if(res.isSuccessful){
                        existingGame = res.body()!!
                        extantGame()
                    }
                }
            }else{
                Toast.makeText(this@CreateGameFragment.requireContext(),"GameId skilaði ekki leik",Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun startGame(){
        if(playerNamesList.size>0 && existingGame!=null){
            lifecycleScope.launch {
                val res = FolfApi.retrofitService.startGame(existingGame!!.id!!.toInt())
                if(res.isSuccessful){
                    val args = Bundle().apply {
                        putInt("GAME_ID", Math.toIntExact(existingGame!!.id!!) )
                    }
                    // Navigate to InputScoreFragment with arguments
                    findNavController().navigate(R.id.action_CreateGameFragment_to_InputScoreFragment, args)
                }else Toast.makeText(requireContext(),"eitthvað vandamál með að byrja þennan leik",Toast.LENGTH_SHORT).show()
            }
        } else {
        // Handle case where no players are added
        Toast.makeText(this@CreateGameFragment.requireContext(), "Please add at least one player", Toast.LENGTH_SHORT).show()
        }
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
                            playerNameAdapter.notifyDataSetChanged()
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
                playerNameAdapter.notifyDataSetChanged()
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