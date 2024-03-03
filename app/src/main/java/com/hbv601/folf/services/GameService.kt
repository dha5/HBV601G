package com.hbv601.folf.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hbv601.folf.Entities.GameEntity
import com.hbv601.folf.Entities.GameParcel
import java.time.LocalDate
import java.time.format.DateTimeFormatter


// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val REGISTER_GAME = "com.hbv.folf.services.action.REGISTER_GAME"
private const val UPDATE_GAME = "com.hbv.folf.services.action.UPDATE_GAME"
private const val FETCH_GAME = "com.hbv.folf.services.action.FETCH_GAME"
private const val ADD_PLAYER = "com.hbv.folf.services.action.ADD_PLAYER"
private const val ADD_PLAYERS = "com.hbv.folf.services.action.ADD_PLAYERS"
private const val FETCH_PLAYER_GAMES = "com.hbv.folf.services.action.FETCH_PLAYER_GAMES"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.hbv601.folf.services.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.hbv601.folf.services.extra.PARAM2"
private const val GAME_TITLE = "com.hbv601.folf.services.extra.GAME_TITLE"
private const val GAME_ID = "com.hbv601.folf.services.extra.GAME_ID"
private const val GAME_COURSE = "com.hbv601.folf.services.extra.GAME_COURSE"
private const val GAME_PLAYER = "com.hbv601.folf.services.extra.GAME_PLAYER"
private const val PLAYER_ARRAY = "com.hbv601.folf.services.extra.PLAYER_ARRAY"
private const val GAME_TIME = "com.hbv601.folf.services.extra.GAME_TIME"
private const val GAME_PARCEL = "com.hbv601.folf.services.extra.GAME_PARCEL"
private const val GAME_PARCEL_ARRAY = "com.hbv601.folf.services.extra.GAME_PARCEL_ARRAY"
private const val RECIEVE_GAMEPARCEL = "com.hbv601.folf.RegisterFragment.GAMEPARCELRECIEVE"
private const val RECIEVE_GAMEARRAY = "com.hbv601.folf.services.extra.RECIEVEGAMEARRAY"

/**
 * IntentService sem heldur utan um aðgerðir tengdar því að skrá leiki, bæta við spilurum í leiki og sækja upplýsingar um leiki
 */
class GameService : IntentService("GameService") {
    private var GamesList = ArrayList<GameEntity>()
    init {
        GamesList.add(GameEntity("New Game","skaftahlíð", LocalDate.now(),"John"))
        GamesList[0].addPlayer("John")
    }


    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            REGISTER_GAME -> {
                println("intent Recieved")
                val registeringPlayer = intent.getStringExtra(GAME_PLAYER)
                val title = intent.getStringExtra(GAME_TITLE)
                val course = intent.getStringExtra(GAME_COURSE)
                val temptime = intent.getStringExtra(GAME_TIME)
                val time = LocalDate.parse(temptime, DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"))
                val gameEntity = handleActionRegisterGame(title,course,time,registeringPlayer)
                if(gameEntity != null){
                    val gameParcel = gameEntity.gameEntityToParcel();
                    val RTReturn: Intent = Intent(RECIEVE_GAMEPARCEL)
                    RTReturn.putExtra(GAME_PARCEL, gameParcel);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)
                }

            }
            UPDATE_GAME ->{
                val gameId = intent.getIntExtra(GAME_ID,-1)
                val title = intent.getStringExtra(GAME_TITLE)
                val course = intent.getStringExtra(GAME_COURSE)
                val time = LocalDate.parse(intent.getStringExtra(GAME_TIME))


                handleActionUpdateGame(gameId,title,course,time)
            }
            ADD_PLAYER ->{
                val gameId = intent.getIntExtra(GAME_ID, -1)
                val player = intent.getStringExtra(GAME_PLAYER)

                handleActionAddPlayer(gameId,player)
            }
            ADD_PLAYERS->{
                val gameId = intent.getIntExtra(GAME_ID,-1)
                val playerList = intent.getStringArrayListExtra(PLAYER_ARRAY)
                if(gameId!=-1){
                    if (playerList != null) {
                        handleActionAddPlayers(gameId,playerList)
                    }
                }
            }
            FETCH_GAME ->{
                val gameId = intent.getIntExtra(GAME_ID,-1)
                if(gameId<0){

                    Log.d("invalid gameId", gameId.toString())
                    return
                }
                handleActionFetchGame(gameId)
            }
            FETCH_PLAYER_GAMES ->{
                val player = intent.getStringExtra(GAME_PLAYER)
                if(player!=null){
                    handleActionFetchPlayerGames(player)
                }
            }
        }
    }

    private fun handleActionFetchPlayerGames(player:String){
        val returnList = ArrayList<GameParcel>()
        for(game in GamesList){
            Log.d("course",game.course)
            //if(game.players.contains(player)||game.creatingPlayer==player){
                returnList.add(game.gameEntityToParcel())
            //}
        }
        val parcelList = GameParcel.newArray(returnList.size)
        var i = 0
        for(parcel in returnList){
            parcelList[i++] = parcel
        }
        val RTReturn: Intent = Intent(RECIEVE_GAMEARRAY)
        RTReturn.putExtra(GAME_PARCEL_ARRAY,parcelList)
        LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)

    }

    private fun handleActionFetchGame(id:Int){
        println("intent Received")
        val gameEntity = GamesList[id];
        val gameParcel = gameEntity.gameEntityToParcel();
        val RTReturn: Intent = Intent(RECIEVE_GAMEPARCEL)
        RTReturn.putExtra(GAME_PARCEL, gameParcel);
        LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionRegisterGame(title: String?, course: String?, time: LocalDate, registeringPlayer: String?) :GameEntity? {
        if(title is String && course is String && registeringPlayer is String){
            val gameEntity = GameEntity(title,course,time,registeringPlayer)
            GamesList.add(gameEntity).also { gameEntity.setId(GamesList.indexOf(gameEntity)) }
            Log.d("course",gameEntity.course)
            return gameEntity



        }
        return null
    }

    /**
     * upfærum leikjarhlut. mun kalla á network service seinna til að tengjast netinu
     */
    private fun handleActionUpdateGame(gameId: Int,title:String?,course:String?, time:LocalDate?) {
        if(gameId < 0) return
        val game  = GamesList[gameId]
        if(title is String) game.updateTitle(title)
        if(course is String) game.updateCourse(course)
        if(time is LocalDate) game.updateTime(time)

        TODO("Handle action Baz")
    }

    private fun handleActionAddPlayer(gameId: Int,player:String?){
        if(gameId < 0 || gameId >= GamesList.size) return
        val game = GamesList[gameId]
        if(player is String) game.addPlayer(player)
        GamesList[gameId] = game
    }
    private fun handleActionAddPlayers(gameId:Int,players: ArrayList<String>){
        if(gameId < 0 || gameId >= GamesList.size) return
        val game = GamesList[gameId]
        for(player in players){
            if(!game.players.contains(player)){
                game.addPlayer(player)
            }
        }
        GamesList[gameId] = game

    }

    companion object {
        /**
         * Starts this service to perform action Foo with the given parameters. If
         * the service is already performing a task this action will be queued.
         *
         * @see IntentService
         */
        // TODO: Customize helper method
        @JvmStatic
        fun startActionRegisterGame(context: Context, registeringPlayer:String, gameTitle:String, course: String, time: String) {
            val intent = Intent(context, GameService::class.java).apply {
                action = REGISTER_GAME
                putExtra(GAME_PLAYER, registeringPlayer)
                putExtra(GAME_TITLE, gameTitle)
                putExtra(GAME_COURSE, course)
                putExtra(GAME_TIME, time)
            }
            context.startService(intent)
        }
        fun startActionUpdateGame(context: Context, gameId:Int,gameTitle:String, time:String,course:String){
            val intent = Intent(context, GameService::class.java).apply{
                action = UPDATE_GAME
                putExtra(GAME_ID,gameId)
                putExtra(GAME_TITLE, gameTitle)
                putExtra(GAME_COURSE, course)
                putExtra(GAME_TIME, time)

            }
            context.startService(intent)
        }

        fun startActionAddPlayer(context: Context, gameId:Int, player:String){
            val intent = Intent(context, GameService::class.java).apply{
                action = ADD_PLAYER
                putExtra(GAME_ID,gameId)
                putExtra(GAME_PLAYER,player)
            }
            context.startService(intent)
        }
        fun startActionAddPlayers(context:Context,gameId: Int,players:ArrayList<String>){
            val intent = Intent(context, GameService::class.java).apply{
                action = ADD_PLAYERS
                putExtra(GAME_ID,gameId)
                putExtra(PLAYER_ARRAY,players)
            }
        }

        fun startActionFetchGame(context: Context, gameId: Int){
            val intent = Intent(context, GameService::class.java).apply{
                action = FETCH_GAME
                putExtra(GAME_ID,gameId)
            }
            context.startService(intent)
        }

        fun startActionFetchPlayerGames(context: Context, player: String){
            val intent = Intent(context, GameService::class.java).apply{
                action = FETCH_PLAYER_GAMES
                putExtra(GAME_PLAYER,player)
            }
            context.startService(intent)
        }

    }
}