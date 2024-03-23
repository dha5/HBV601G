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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.Entities.UserEntity
import com.hbv601.folf.databinding.FragmentCreateGameBinding
import com.hbv601.folf.network.FolfApi
import com.hbv601.folf.services.GameService
import kotlinx.coroutines.launch


class CreateGameFragment : Fragment(), AdapterView.OnItemSelectedListener{
    //intent service deprecated, rethink
    /*private val REGISTER_GAME = "com.hbv.folf.services.action.REGISTER_GAME"
    private val UPDATE_GAME = "com.hbv.folf.services.action.UPDATE_GAME"
    private val FETCH_GAME = "com.hbv.folf.services.action.FETCH_GAME"
    private val ADD_PLAYER = "com.hbv.folf.services.action.ADD_PLAYER"*/
    private val GAME_PARCEL = "com.hbv601.folf.services.extra.GAME_PARCEL"
    private val RECIEVE_GAMEPARCEL = "com.hbv601.folf.RegisterFragment.GAMEPARCELRECIEVE"
    private var gameId:Number? = null
    private var selectedCourse:String? = null
    private var selectedCourseId:Int? = null
    private var selectedPlayer:String? = null
    private var courseIds:ArrayList<Int>? = null
    private var friendsList: ArrayList<String>? = null
    private var friends: ArrayList<UserEntity>? = null

    private val bReceiver: BroadcastReceiver = object : BroadcastReceiver() {
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
    var bManager: LocalBroadcastManager? = null
    private val calendar = Calendar.getInstance()
    private var _binding: FragmentCreateGameBinding? = null
    private val binding get() = _binding!!
    private val playerNamesList = mutableListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        bManager = this.getContext()?.let { LocalBroadcastManager.getInstance(it) }
        val intentFilter = IntentFilter()
        intentFilter.addAction(RECIEVE_GAMEPARCEL)
        bManager!!.registerReceiver(bReceiver, intentFilter)
        _binding = FragmentCreateGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val gameParcel = arguments?.getParcelable("GAME_PARCEL",GameParcel::class.java)
        if(gameParcel != null){
            binding.textViewCreateGame.text = "Update Game"
            binding.timeField.setText(gameParcel.time)

            //binding.locationField.setText(gameParcel.course)
        }else {
            // Top layer
            binding.textViewCreateGame.text = "Create a new game"
        }
        // Second layer
        binding.gameNowButton.setOnClickListener {
            // Set current date and time to timeField
            binding.timeField.setText(java.time.LocalDateTime.now().toString())
        }

        // Third layer
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, playerNamesList)
        binding.playerListView.adapter = adapter
        if(gameParcel != null){
            for(player in gameParcel.players!!){
                playerNamesList.add(player)
            }
        }
        binding.addPlayerButton.setOnClickListener {
            val playerName = binding.playerField.text.toString().trim()
            if (playerName.isNotBlank()) {
                playerNamesList.add(playerName)
                adapter.notifyDataSetChanged()
                binding.playerField.text.clear()
                if(gameId!=null){
                    this.context?.let { it1 -> GameService.startActionAddPlayer(it1, gameId!!.toInt(),playerName) }
                }
            }
        }
        getFriends()
        binding.addPlayerSpinnerButton.setOnClickListener {
            if(selectedPlayer!=null){
                playerNamesList.add(selectedPlayer!!)
                adapter.notifyDataSetChanged()
                if(gameId!=null){
                    this.context?.let { it1 -> GameService.startActionAddPlayer(it1, gameId!!.toInt(),selectedPlayer!!) }
                }
            }
        }
        getCourses()

        // Fourth layer
        if(gameId==null){
            binding.registerGameButton.setOnClickListener {
                Log.d("TAG", "register game button")
                //val course = binding.locationField.text.toString()
                val course = selectedCourse
                val time = binding.timeField.text.toString()
                val gameTitle = "no title"
                val testPlayer = "John"
                if(course != null) {
                    this.context?.let { it1 ->
                        GameService.startActionRegisterGame(
                            it1,
                            testPlayer,
                            gameTitle,
                            course,
                            time
                        )
                    }
                }
            }
        }else{
            updateButton()

        }

        binding.startGameButton.setOnClickListener {
            if (playerNamesList.isNotEmpty()) {

                // Create bundle with playerNamesList
                val args = Bundle().apply {
                    if(gameId!= null){
                        putInt("gameId", gameId!!.toInt())
                    }
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
    fun getFriends(){
        //implement call to get friends
        val spinner = binding.playerSpinner
        lifecycleScope.launch {
            Log.d("accesstoken",requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)!!)
            val bearerToken = "Bearer ${requireActivity().getSharedPreferences("USER",0).getString("AccessToken",null)!!}"
            val res = FolfApi.retrofitService.getFriends("Bearer ${bearerToken}")
            if(res.isSuccessful && res.body() != null){
                Log.d("friends","it works")
                val friends = res.body()
                for(friend in friends){
                    
                }
            }
        }
        ArrayAdapter.createFromResource(this.requireContext(),R.array.placeholderPlayers,android.R.layout.simple_spinner_item).also {
            adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener =this

    }
    fun getCourses(){
        val spinner = binding.locationField
        lifecycleScope.launch {
            val courses = FolfApi.retrofitService.getFields()
            val names = ArrayList<String>()
            courseIds = ArrayList<Int>()
            if(courses.isSuccessful && courses.body()!=null) {
                for (course in courses.body()!!) {
                    names.add(course.name)
                    courseIds!!.add(course.id)
                }
            }
            val arrayAdapter = ArrayAdapter<String>(this@CreateGameFragment.requireContext(),
                android.R.layout.simple_spinner_dropdown_item,names)
            spinner.adapter = arrayAdapter
            spinner.onItemSelectedListener = this@CreateGameFragment
        }
    }
    fun updateButton(){
        binding.registerGameButton.setText("Update")
        binding.registerGameButton.setOnClickListener {
            Log.d("tag","update game button")
            val course = selectedCourse
            val time = binding.timeField.text.toString()
            val gameTitle = "no title"
            if(course != null) {
                this.context?.let { it1 ->
                    GameService.startActionUpdateGame(
                        it1,
                        gameId!!.toInt(),
                        gameTitle,
                        time,
                        course
                    )
                }
            }
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
                    selectedPlayer = parent.getItemAtPosition(position).toString()
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