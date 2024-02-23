package com.hbv601.folf.services

import android.app.IntentService
import android.content.Intent
import android.content.Context
import com.hbv601.folf.Entities.GameEntity
import java.sql.Date


// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val REGISTER_GAME = "com.hbv.folf.services.action.REGISTER_GAME"
private const val UPDATE_GAME = "com.hbv.folf.services.action.UPDATE_GAME"
private const val FETCH_GAME = "com.hbv.folf.services.action.FETCH_GAME"

// TODO: Rename parameters
private const val EXTRA_PARAM1 = "com.hbv601.folf.services.extra.PARAM1"
private const val EXTRA_PARAM2 = "com.hbv601.folf.services.extra.PARAM2"
private const val GAME_TITLE = "com.hbv601.folf.services.extra.GAME_TITLE"
private const val GAME_ID = "com.hbv601.folf.services.extra.GAME_ID"
private const val GAME_COURSE = "com.hbv601.folf.services.extra.GAME_COURSE"
private const val GAME_PLAYER = "com.hbv601.folf.services.extra.GAME_PLAYER"
private const val GAME_TIME = "com.hbv601.folf.services.extra.GAME_PLAYER"

/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class GameService : IntentService("GameService") {
    private lateinit var GamesList: ArrayList<GameEntity>;

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            REGISTER_GAME -> {
                val title = intent.getStringExtra(GAME_TITLE)
                val course = intent.getStringExtra(GAME_COURSE)
                val time = Date.valueOf(intent.getStringExtra(GAME_TIME))
                val registeringPlayer = intent.getStringExtra(GAME_PLAYER)

                handleActionRegisterGame(title,course,time,registeringPlayer)
            }
            UPDATE_GAME ->{

            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionRegisterGame(title: String?, course: String?, time: Date?, registeringPlayer: String?) {
        if(title is String && course is String && time is Date && registeringPlayer is String){
            val GameEntity = GameEntity(title,course,time,registeringPlayer)
            GamesList.add(GameEntity).also { GameEntity.setId(GamesList.indexOf(GameEntity)) }
        }
        TODO("Handle action Foo")
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionUpdateGame(param1: String?, param2: String?) {
        TODO("Handle action Baz")
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
        fun startActionUpdateGame(context: Context, gameId:Number,gameTitle:String, time:String,course:String){
            val intent = Intent(context, GameService::class.java).apply{
                action = UPDATE_GAME
                putExtra(GAME_ID,gameId)
                putExtra(GAME_TITLE, gameTitle)
                putExtra(GAME_COURSE, course)
                putExtra(GAME_TIME, time)

            }
            context.startService(intent)
        }


    }
}