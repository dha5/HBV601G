package com.hbv601.folf.services

import android.app.IntentService
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hbv601.folf.Entities.ScoreEntity

// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
private const val UPDATE_SCORE = "com.hbv601.folf.services.action.UPDATESCORE"
private const val FETCH_SCORES = "com.hbv601.folf.services.action.FETCHSCORE"
private const val FETCH_PLAYER_SCORE = "com.hbv601.folf.services.action.FETCHPLAYERSCORE"
private const val POST_SCORES = "com.hbv601.folf.services.action.POSTSCORES"
private const val POST_SCORE = "com.hbv601.folf.services.action.POSTSCORE"
private const val CREATE_SCOREENTITY = "com.hbv601.folf.services.action.CREATESCOREENTITY"

private const val RECIEVE_SCORE_PARCEL = "com.hbv601.folf.services.action.RECIEVE_SCORE_PARCEL"
private const val RECIEVE_PLAYER_SCORE = "com.hbv601.folf.services.action.RECIEVE_PLAYER_SCORE"

// TODO: Rename parameters
private const val SCORE_PARCEL = "com.hbv601.folf.services.extra.SCOREPARCEL"
private const val SCORE_ARRAY = "com.hbv601.folf.services.extra.SCOREARRAY"
private const val PLAYER_ARRAY = "com.hbv601.folf.services.extra.PLAYERARRAY"
private const val GAME_ID = "com.hbv601.folf.services.extra.GAMEID"
private const val PLAYER = "com.hbv601.folf.services.extra.PLAYER"
private const val SCORE = "com.hbv601.folf.services.extra.SCORE"
/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class ScoreService : IntentService("ScoreService") {
    private val scoreHashMap = HashMap<Int,ScoreEntity>()

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            CREATE_SCOREENTITY -> {
                val gameId = intent.getIntExtra(GAME_ID,-1)
                val playerList = intent.getStringArrayListExtra(PLAYER_ARRAY)
                if(gameId>-1) {
                    if (playerList != null) {
                        handleActionCreateScore(gameId, playerList)
                    }
                }
            }

            POST_SCORE -> {
                val gameId = intent.getIntExtra(GAME_ID, -1)
                val player = intent.getStringExtra(PLAYER)
                val score = intent.getIntExtra(SCORE, -1)
                if(gameId>=0&&score>0){
                    handleActionPostScore(gameId, player,score)
                }

            }
            FETCH_SCORES ->{
                val gameId = intent.getIntExtra(GAME_ID,-1)
                if(gameId>-1){
                    handleActionFetchScores(gameId)
                }
            }
            FETCH_PLAYER_SCORE ->{
                val gameId = intent.getIntExtra(GAME_ID,-1)
                val player = intent.getStringExtra(PLAYER)
                if(gameId>-1 && player != null){
                    handleActionFetchPlayerScore(gameId,player)
                }
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionCreateScore(gameId:Int, playerList: ArrayList<String>) {
        if(scoreHashMap.containsKey(gameId)){
            val scoreEntity = ScoreEntity(gameId,null,playerList)
            scoreHashMap.put(gameId,scoreEntity)
        }
        TODO("Handle action Foo")
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private fun handleActionPostScore(gameId:Int, player: String?,score:Int) {
        if(scoreHashMap.containsKey(gameId)){
            if (player != null) {
                scoreHashMap[gameId]!!.addScore(player,score)
            }
        }
        TODO("Handle action Baz")
    }

    private fun handleActionFetchScores(gameId: Int){
        if(scoreHashMap.containsKey(gameId)) {
            val scoreEntity = scoreHashMap[gameId]
            val scoreParcel = scoreEntity?.ScoreEntityToParcel()
            val RTReturn: Intent = Intent(RECIEVE_SCORE_PARCEL)
            RTReturn.putExtra(SCORE_PARCEL, scoreParcel);
            LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)
        }
    }

    private fun handleActionFetchPlayerScore(gameId: Int, player: String){
        if(scoreHashMap.containsKey(gameId)) {
            val scoreEntity = scoreHashMap[gameId]
            val playerScore = scoreEntity!!.getPlayerScore(player)
            val RTReturn: Intent = Intent(RECIEVE_PLAYER_SCORE)
            RTReturn.putExtra(SCORE,playerScore)
            LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)

        }
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
        fun startActionCreateScore(context: Context, gameId: Int, playerList: ArrayList<String>) {
            val intent = Intent(context, ScoreService::class.java).apply {
                action = CREATE_SCOREENTITY
                putExtra(GAME_ID, gameId)
                putExtra(PLAYER_ARRAY, playerList)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionPostScore(context: Context, gameId: Int,player: String?, score: Int) {
            val intent = Intent(context, ScoreService::class.java).apply {
                action = POST_SCORE
                putExtra(GAME_ID, gameId)
                putExtra(PLAYER, player)
                putExtra(SCORE, score)
            }
            context.startService(intent)
        }
    }
}