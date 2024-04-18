package com.hbv601.folf
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.hbv601.folf.databinding.FragmentHomePageBinding


class HomePageFragment : Fragment() {
    private var _binding: FragmentHomePageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_home_page, container, false)



        _binding = FragmentHomePageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val textView = view.findViewById<TextView>(R.id.textMessage)

        /**
        val dummycourse = CourseEntity.generateDummy()
        val distancefrom = dummycourse.getDistanceFrom(this.activity);
        Log.d("distancefrom",distancefrom.toString())

        //Notað til að testa CourseEntity classann
        */
        val userName = requireActivity().getSharedPreferences("USER",0).getString("Name","")!! // þarf að skipta út fyrir notendanafn


        val welcomeMessage = getString(R.string.home_page_welcome_message, userName)
        if(textView != null) {
            textView.text = welcomeMessage
        }
        super.onViewCreated(view, savedInstanceState)
        binding.buttonNewGame.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_CreateGameFragment)
        }


        super.onViewCreated(view, savedInstanceState)
        binding.buttonViewCourses.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_CourseFragment)
        }

        binding.buttonYourGames.setOnClickListener{
            findNavController().navigate(R.id.action_homePageFragment_to_YourGamesFragment)
            //navigate to gameslist
        }

        binding.buttonFriends.setOnClickListener {
            findNavController().navigate(R.id.action_homePageFragment_to_FriendsFragment)
        }
        binding.logOutButton.setOnClickListener {
            findNavController().navigate(R.id.action_homepage_to_firstFragment)
        }
    }

}
