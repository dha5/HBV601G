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

import com.hbv601.folf.Entities.RegisterUser
import com.hbv601.folf.databinding.FragmentRegisterBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class RegisterFragment : Fragment(){

    private val TAG = "RegisterFragment"
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
            registerUser()

        }
    }


    private fun registerUser() {
        lifecycleScope.launch {

            try {
                val res = FolfApi.retrofitService.doRegister(
                    RegisterUser(
                        binding.fullnameEditText.text.toString(),
                        binding.usernameEditText.text.toString(),
                        binding.passwordEditText.text.toString()
                    )
                )

                Log.d(TAG, "ÞETTA ER REGISTERBUTTONCLICK")
                Log.d(TAG, res.toString())

                if (res.isSuccessful && res.body() != null) {
                    findNavController().navigate(R.id.action_registerFragment_to_FirstFragment)
                }
                else {
                    val text = "Registration was not successful!"
                    val duration = Toast.LENGTH_LONG
                    val toast = Toast.makeText(this@RegisterFragment.context, text, duration)
                    toast.show()
                }
            }
            catch (e: IOException) {
                Log.e(TAG, "Probably no Internet connection! $e")
            }
            catch (e: HttpException) {
                Log.e(TAG, "HTTP error: $e")
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}