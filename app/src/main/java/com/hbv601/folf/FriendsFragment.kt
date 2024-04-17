package com.hbv601.folf

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
import com.hbv601.folf.Entities.UserEntity
import com.hbv601.folf.databinding.FragmentFriendsBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch

class FriendsFragment:  Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentFriendsBinding? = null;
    private val binding get() = _binding!!
    private var friendsList = ArrayList<String>()
    private var friendsIds = ArrayList<Long>()
    private var userList = ArrayList<String>()
    private var userIds = ArrayList<Long>()
    private var hatedUser: UserEntity? = null
    private var newFriend: UserEntity? = null
    lateinit var listAdapter:ArrayAdapter<String>
    val ACCESTOKEN = "AccessToken"

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFriendsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch{
            getFriends()
            getUsers()
        }
        binding.AddUserButton.setOnClickListener { lifecycleScope.launch{
            addFriend()
        } }
        if(friendsList.size==0 )run {
            binding.enemyDiv.visibility
        }
        binding.RemoveFriendButton.setOnClickListener{
            lifecycleScope.launch {
                removeFriend()
            }
        }

    }
    suspend fun getFriends(){
        lifecycleScope.launch{
        val preferences = requireActivity().getSharedPreferences("USER",0)
        if(preferences != null){
            val bearerToken = preferences.getString(ACCESTOKEN,null)
            if(bearerToken!=null){

                val myFriends = FolfApi.retrofitService.getFriends("Bearer ${bearerToken}")
                Log.d("friends",myFriends.toString())
                if(!myFriends.isSuccessful){
                    Toast.makeText(requireContext(),
                        "Ekki tókst að sækja vinalista",Toast.LENGTH_SHORT).show()
                    return@launch
                }
                if(myFriends.body()!=null){
                    val friends = myFriends.body()!!
                    friendsList = ArrayList<String>()
                    friendsIds = ArrayList<Long>()
                    for (friend in friends){
                        Log.d("friendslist",friend.toString())
                        if(friend.username in userList){
                            userList.remove(friend.username)
                            userIds.remove(friend.id)
                        }
                        friendsList.add(friend.username)
                        friendsIds.add(friend.id)
                    }
                }

                val arrayAdapter = ArrayAdapter<String>(this@FriendsFragment.requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,friendsList)
                binding.FriendsSpinner.adapter = arrayAdapter
                binding.FriendsSpinner.onItemSelectedListener = this@FriendsFragment
                listAdapter = ArrayAdapter<String>(this@FriendsFragment.requireContext(),
                    android.R.layout.simple_list_item_1,friendsList)
                binding.friendsList.adapter = listAdapter


            }
        }}
    }
    suspend fun getUsers(){
        lifecycleScope.launch {
            val res = FolfApi.retrofitService.getUsers()
            Log.d("friends",res.toString())
            if (!res.isSuccessful) {
                Toast.makeText(
                    requireContext(),
                    "Ekki tókst að sækja lista af notendum", Toast.LENGTH_SHORT
                ).show()
                return@launch
            }
            val users = res.body()!!
            userList = ArrayList<String>()
            userIds = ArrayList<Long>()
            for (user in users) {
                if (user.id in friendsIds) continue
                userList.add(user.username)
                userIds.add(user.id)
            }
            val arrayAdapter = ArrayAdapter<String>(
                this@FriendsFragment.requireContext(),
                android.R.layout.simple_spinner_dropdown_item, userList
            )
            binding.UserListSpinner.adapter = arrayAdapter
            binding.UserListSpinner.onItemSelectedListener = this@FriendsFragment
        }
    }
    suspend fun addFriend(){
        if(newFriend == null){
            Toast.makeText(this@FriendsFragment.requireContext(),
                "Vinsamlegast veljið vin",Toast.LENGTH_SHORT).show()
            return
        }
        val preferences = requireActivity().getSharedPreferences("USER",0)
        val bearerToken = preferences.getString(ACCESTOKEN,null)
        if(bearerToken == null){
            Toast.makeText(this@FriendsFragment.requireContext(),
                "Ekkert login token fannst",Toast.LENGTH_SHORT).show()
            return
        }
        val res = FolfApi.retrofitService.addFriend("Bearer ${bearerToken!!}",newFriend!!)
        if(res.isSuccessful){
            Toast.makeText(this@FriendsFragment.requireContext(),
                "${newFriend} bætt við í vinalista",Toast.LENGTH_SHORT).show()
            friendsList.add(newFriend!!.username)
            listAdapter.notifyDataSetChanged()
        }else{
            Toast.makeText(this@FriendsFragment.requireContext(),
                "Ekki tókst að bæta vin í vinalista",Toast.LENGTH_SHORT).show()
        }
    }
    suspend fun removeFriend(){
        if(hatedUser == null){
            Toast.makeText(this@FriendsFragment.requireContext(),
                "Vinsamlegast veljið vin (sem er ekki lengur vinur)",Toast.LENGTH_SHORT).show()
            return
        }
        val preferences = requireActivity().getSharedPreferences("USER",0)
        val bearerToken = preferences.getString(ACCESTOKEN,null)
        if(bearerToken == null){
            Toast.makeText(this@FriendsFragment.requireContext(),
                "Ekkert login token fannst",Toast.LENGTH_SHORT).show()
            return
        }
        val res = FolfApi.retrofitService.deleteFriend("Bearer ${bearerToken!!}",hatedUser!!)
        Log.d("friends",res.toString())
        if(res.isSuccessful){
            Toast.makeText(this@FriendsFragment.requireContext(),
                    "${hatedUser} fjarlægðir úr vinalista. megi hann rotna í helvíti (eða kvíla í friði eftir þörfum)",Toast.LENGTH_SHORT).show()
            friendsList.remove(hatedUser!!.username)
            listAdapter.notifyDataSetChanged()
        }else{
            Toast.makeText(this@FriendsFragment.requireContext(),
                "Ekki tókst að fjarlægja vin af lista, kannski eru það forlög",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when(parent!!.id){
            R.id.UserListSpinner ->{
                newFriend = UserEntity(userIds[position],null,userList[position])

            }
            R.id.FriendsSpinner ->{
                hatedUser = UserEntity(friendsIds[position],null,friendsList[position])
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        when(parent!!.id){
            R.id.UserListSpinner ->{
                newFriend = null
            }
            R.id.FriendsSpinner ->{
                hatedUser = null
            }
        }
    }

}