package com.hbv601.folf
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.hbv601.folf.ViewHolders.CourseViewHolder
import com.hbv601.folf.ViewHolders.ScoreViewHolder
import com.hbv601.folf.databinding.CourseItemBinding
import com.hbv601.folf.databinding.FragmentCoursesBinding
import com.hbv601.folf.databinding.ScoreItemBinding

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

        // Observe courses LiveData
        viewModel.courses.observe(viewLifecycleOwner) { courses ->
            // Update UI or perform any necessary actions based on LiveData changes
        }

        // Fetch courses
        val courses = viewModel.fetchCourses()
        //val courses = viewModel.courses.value
        if(courses != null){
            for(course in courses){
                Log.d("courseName",course.courceName)
                val courseView = CourseViewHolder(CourseItemBinding.inflate(layoutInflater))
                this.context?.let { courseView.bindItem(course, it) }
                /*for(game in course.games){
                    val gameView = GameItemViewHolder(GameItemBinding.inflate(layoutInflater))
                    gameView.bindGameClass(game)
                    courseView.addGame(gameView.itemView)
                }*/
                val view = getBestScore(course.courceName)
                if(view!=null) {
                    courseView.bestScore(view)
                }
                binding.CourseList.addView(courseView.itemView)
            }
        }
    }
    fun getBestScore(course:String):View?{
        val scoreView = ScoreViewHolder(ScoreItemBinding.inflate(layoutInflater))
        scoreView.onBind("Strákafrolf í hádeginu","24. Mars 2023 12:30",22,21)
        return scoreView.itemView
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}