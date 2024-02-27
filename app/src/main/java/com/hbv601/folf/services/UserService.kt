package com.hbv601.folf.services
import android.app.IntentService
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.hbv601.folf.Entities.UserEntity
private const val REGISTER_USER = "com.hbv601.folf.services.action.REGISTERUSER"
private const val SIGN_IN = "com.hbv601.folf.services.action.SIGNIN"
private const val FETCH_ALL_USERS = "com.hbv601.folf.services.action.FETCHALLUSERS"

private const val RECIEVE_ALL_USERS = "com.hbv601.folf.services.action.RECIEVEALLUSERS"
private const val USER_REGISTERED = "com.hbv601.folf.services.action.USERREGISTERED"
private const val USER_REGISTER_FAIL = "com.hbv601.folf.services.action.USERREGISTEREDFAIL"
private const val USER_SIGNED_IN = "com.hbv601.folf.services.action.USERSIGNEDIN"
private const val SIGN_FAIL_NO_SUCH_USER = "com.hbv601.folf.services.action.SIGNFAILNOSUCHUSER"
private const val SIGN_FAIL_WRONG_PASSWORD = "com.hbv601.folf.services.action.SIGNFAILWRONGPASSWORD"

private const val USERNAME = "com.hbv601.folf.services.extra.USERNAME"
private const val PASSWORD = "com.hbv601.folf.services.extra.PASSWORD"
private const val USERLIST = "com.hbv601.folf.services.extra.USERLIST"

class UserService :IntentService("UserActionService"){
    private val userHashMap = HashMap<String,UserEntity>()

    override fun onHandleIntent(intent: Intent?) {
        when (intent?.action) {
            REGISTER_USER->{
                val username = intent.getStringExtra(USERNAME)
                val password = intent.getStringExtra(PASSWORD)
                actionHandleRegisterUser(username,password)
            }
            SIGN_IN->{
                val username = intent.getStringExtra(USERNAME)
                val password = intent.getStringExtra(PASSWORD)
            }
            FETCH_ALL_USERS->{
                actionHandleFetchAllUsers()
            }
        }
    }

    fun actionHandleRegisterUser(username:String?,password:String?){
        if(!userHashMap.containsKey(username)){
            val userEntity = UserEntity(username!!, password!!)
            userHashMap[username] = userEntity
            val RTReturn: Intent = Intent(USER_REGISTERED)
            LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)
        }else {
            val RTReturn: Intent = Intent(USER_REGISTER_FAIL)
            LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)
        }
    }
    fun actionHandleSignInUser(username:String,password:String){
        val userEntity = userHashMap[username]
        if(userEntity != null){
            if(userEntity.comparePassword(password)){
                val RTReturn: Intent = Intent(USER_SIGNED_IN)
                LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)
            }
            val RTReturn: Intent = Intent(SIGN_FAIL_WRONG_PASSWORD)
            LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)
        }
        val RTReturn: Intent = Intent(SIGN_FAIL_NO_SUCH_USER)
        LocalBroadcastManager.getInstance(this).sendBroadcast(RTReturn)
    }
    fun actionHandleFetchAllUsers(){
        val Users = userHashMap.keys
        val retUsers = ArrayList<String>()
        for(user in Users){
            retUsers.add(user)
        }
        val RTReturn: Intent= Intent(RECIEVE_ALL_USERS)
        RTReturn.putExtra(USERLIST,retUsers)
    }


    companion object {
        @JvmStatic
        fun startActionUserRegistry(context: Context, username:String, password:String) {
            val intent = Intent(context, ScoreService::class.java).apply {
                action = REGISTER_USER
                putExtra(USERNAME,username)
                putExtra(PASSWORD,password)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionUserSignIn(context: Context, username:String,password:String) {
            val intent = Intent(context, ScoreService::class.java).apply {
                action = SIGN_IN
                putExtra(USERNAME,username)
                putExtra(PASSWORD,password)
            }
            context.startService(intent)
        }

        @JvmStatic
        fun startActionFetchAllUsers(context: Context){
            val intent = Intent(context, ScoreService::class.java).apply {
                action = FETCH_ALL_USERS
            }
            context.startService(intent)
        }
    }
}
