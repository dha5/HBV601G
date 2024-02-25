package com.hbv601.folf
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.databinding.FragmentRegisterBinding

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

            Log.d("Lykilorð",password)
            Log.d("Notendanafn", username)


            findNavController().navigate(R.id.action_registerFragment_to_FirstFragment)
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}