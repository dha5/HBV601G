package com.hbv601.folf
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hbv601.folf.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_page, container, false)

        val textView = view.findViewById<TextView>(R.id.textMessage)

        val userName = "John" // þarf að skipta út fyrir notendanafn
        val numInvitations = 5 // þarf að skipta út fyrir fjölda invites sem notandi hefur fengið
        val numOpenMatches = 10 // þarf að skipta út fyrir fjölda opnra leikja

        val welcomeMessage = getString(R.string.home_page_welcome_message, userName, numInvitations, numOpenMatches)
        if(textView != null) {
            textView.text = welcomeMessage
        }

        binding.buttonYourGames.setOnClickListener{
            //navigate to gameslist
        }
        return view
    }

}
