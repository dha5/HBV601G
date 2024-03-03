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
import com.hbv601.folf.Entities.UserCreds
import com.hbv601.folf.databinding.FragmentLoginBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

const val TAG = "LoginFragment"

class LoginFragment: Fragment() {

    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {


        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //val viewModel: LoginViewModel by viewModels()
        binding.buttonLogin.setOnClickListener {
            checkLoginCredentials()
        }
    }


    private fun checkLoginCredentials() {
        lifecycleScope.launch {
            try {
                val res = FolfApi.retrofitService.doLogin(
                    UserCreds(
                        binding.editTextUsername.text.toString(),
                        binding.editTextPassword.text.toString()
                    )
                )

                Log.d(TAG, "ÞETTA ER LOGINBUTTONCLICK")
                Log.d(TAG, res.toString())

                if (res.isSuccessful && res.body() != null) {
                    Log.d(TAG, res.body()!!.accessToken)
                    findNavController().navigate(R.id.action_LoginFragment_to_HomePageFragment)
                }
                else {
                    val text = "Wrong username or password!"
                    val duration = Toast.LENGTH_LONG
                    val toast = Toast.makeText(this@LoginFragment.context, text, duration)
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
