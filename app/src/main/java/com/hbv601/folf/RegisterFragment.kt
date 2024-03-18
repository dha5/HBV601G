package com.hbv601.folf
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.Entities.UserRegisterCreds
import com.hbv601.folf.databinding.FragmentRegisterBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch


class RegisterFragment : Fragment(){


    private var _binding: FragmentRegisterBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.registerButton.setOnClickListener {
            //þarf að útfæra, skrá notenda í gagnagrunninn ef hann er ekki til
            var password : String
            var username : String

            password = binding.passwordEditText.text.toString()
            username = binding.usernameEditText.text.toString()
            registerPlayer(username,password)

        }
    }

    private fun registerPlayer(username:String,password:String){
        lifecycleScope.launch {

            val res = FolfApi.retrofitService.doRegister(UserRegisterCreds(null,username,password))
            if(res.isSuccessful && res.body() != null){
                Log.d("registerPlayer","registration succesful")
                Log.d("registered player", res.body()!!.username)
                Toast.makeText(this@RegisterFragment.context,"user ${res.body()!!.username} created",Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.action_registerFragment_to_FirstFragment)
            }else{
                Log.d("registerPlayer","registration failed, user exists already")
                Toast.makeText(this@RegisterFragment.context,"registration failure",Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}