package com.hbv601.folf;

import android.app.Activity;
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.databinding.FragmentLoginBinding

public class LoginFragment: Fragment() {


    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            if (checkLoginCredentials(binding.editTextUsername.text.toString(), binding.editTextPassword.text.toString()))
            findNavController().navigate(R.id.action_LoginFragment_to_UserFragment)
                print("success")
        }
    }

    /**
     * Þarf að útfæra betur. þarf að tala við bakenda einhvernvegin
     */
    private fun checkLoginCredentials(username: String, password: String): Boolean {
        println(username)
        println(password)
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
