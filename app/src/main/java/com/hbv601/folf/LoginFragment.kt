package com.hbv601.folf;

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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

    @SuppressLint("SuspiciousIndentation") //á meðan við erum ekki búnir að útfæra bakendatengingu
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonLogin.setOnClickListener {
            if (checkLoginCredentials(binding.editTextUsername.text.toString(), binding.editTextPassword.text.toString())) {
                var password = binding.editTextPassword.text.toString()
                var username = binding.editTextUsername.text.toString()

                findNavController().navigate(R.id.action_LoginFragment_to_userFragment)

            }
        }
    }

    /**
     * Þarf að útfæra betur. þarf að tala við bakenda einhvernvegin
     */
    private fun checkLoginCredentials(username: String, password: String): Boolean {
        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
