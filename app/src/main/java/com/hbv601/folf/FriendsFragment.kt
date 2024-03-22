package com.hbv601.folf

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.hbv601.folf.databinding.FragmentFriendsBinding
import com.hbv601.folf.network.FolfApi

class FriendsFragment:  Fragment(), AdapterView.OnItemSelectedListener {

    private var _binding: FragmentFriendsBinding? = null;
    private val binding get() = _binding!!
    private var friendsList = ArrayList<String>()
    private var friendsIds = ArrayList<Int>()
    private var userList = ArrayList<String>()
    private var userIds = ArrayList<Long>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
    suspend fun getFriends(){
        val preferences = requireActivity().getSharedPreferences("USER",0)
        if(preferences != null){
            val bearerToken = preferences.getString("accesstoken",null)
            if(bearerToken!=null){

                val myFriends = FolfApi.retrofitService.getFriends(bearerToken)
                if(!myFriends.isSuccessful){
                    Toast.makeText(requireContext(),
                        "Ekki tókst að sækja vinalista",Toast.LENGTH_SHORT).show()
                    return
                }
            }
        }
    }
    suspend fun getUsers(){
        val res = FolfApi.retrofitService.getUsers()
        if(!res.isSuccessful){
            Toast.makeText(requireContext(),
                "Ekki tókst að sækja lista af notendum",Toast.LENGTH_SHORT).show()
            return
        }
        val users = res.body()!!
        userList = ArrayList<String>()
        userIds = ArrayList<Long>()
        for (user in users){
            userList.add(user.username)
            userIds.add(user.id)
        }
        val arrayAdapter = ArrayAdapter<String>(this@FriendsFragment.requireContext(),
            android.R.layout.simple_spinner_dropdown_item,userList)
        binding.UserListSpinner.adapter = arrayAdapter
        binding.UserListSpinner.onItemSelectedListener = this@FriendsFragment
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        TODO("Not yet implemented")
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        TODO("Not yet implemented")
    }

}