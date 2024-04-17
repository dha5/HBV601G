package com.hbv601.folf
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hbv601.folf.Entities.CourseData
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.ViewHolders.CourseViewHolder
import com.hbv601.folf.ViewHolders.ScoreViewHolder
import com.hbv601.folf.databinding.CourseItemBinding
import com.hbv601.folf.databinding.FragmentCoursesBinding
import com.hbv601.folf.databinding.ScoreItemBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch

class CourseFragment : Fragment() {

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CourseViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this)[CourseViewModel::class.java]
        _binding?.viewModel = viewModel
        _binding?.lifecycleOwner = viewLifecycleOwner // Ensure you have the correct lifecycle owner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.courses.observe(viewLifecycleOwner) { courses ->
        }

        lifecycleScope.launch {

            val resCourses = FolfApi.retrofitService.getFields()
            val courses = resCourses.body()
            //val courses = viewModel.courses.value
            if (courses != null) {
                for (course in courses) {
                    Log.d("courseName", course.name)
                    val courseView = CourseViewHolder(CourseItemBinding.inflate(layoutInflater))

                    requireContext().let { courseView.bindItem(course, it) }
                    /*for(game in course.games){
                    val gameView = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
                    gameView.bindGameClass(game)
                    courseView.addGame(gameView.itemView)
                }*/
                    /*val view = getBestScore(course)
                    if (view != null) {
                        courseView.bestScore(view)
                    }*/
                    binding.CourseList.addView(courseView.itemView)
                }
            }
        }
    }


    /**
     * Skilar View til að setja inn í courseView.bestScore
     */
    suspend fun getBestScore(course:CourseData):View?{
        val scoreView = ScoreViewHolder(ScoreItemBinding.inflate(layoutInflater))


        val gamesOnThisCourse = ArrayList<GameData>()
        var bestgame : GameData? = null
        var bestScore = Int.MAX_VALUE
        var par = 0
        val resHoleData = FolfApi.retrofitService.getHolesByFieldId(course.id)
        if (resHoleData.isSuccessful) {
            val holeData = resHoleData.body()
            if (holeData != null) {
                for (hole in holeData){
                    par += hole.par
                }
            }

            val bearerToken =
                requireActivity().getSharedPreferences("USER", 0).getString("AccessToken", null)
            val resUserGames = FolfApi.retrofitService.getYourPastGames("Bearer ${bearerToken}")

            if (resUserGames.isSuccessful) {
                var userGames = resUserGames.body()
                if (userGames != null) {
                    for (game in userGames) {
                        if (game.id?.toInt() == course.id) {
                            gamesOnThisCourse.add(game)
                        }
                    }
                    /*
                    for (game in gamesOnThisCourse) {
                        var throws = 0
                        if (game.id != null) {
                            val resGameScores = FolfApi.retrofitService.getScoreByGameId(game.id)
                            if (resGameScores.isSuccessful) {
                                val gameScores = resGameScores.body()
                                if (gameScores != null) {
                                    for (score in gameScores) {
                                        throws += (score.strokes).toInt()
                                    }
                                }
                            }
                        }
                        if (throws < bestScore){
                            bestScore = throws
                            bestgame = game
                        }
                    }*/
                    if (bestgame != null) {
                        scoreView.onBind(bestgame.name, bestgame.date_created, bestScore, par)
                        return scoreView.itemView
                    }
                }
            }
        }



        //scoreView.onBind("Strákafrolf í hádeginu","24. Mars 2023 12:30",22,21)
        Log.d("getBestScore","skilar null")
        return null

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}