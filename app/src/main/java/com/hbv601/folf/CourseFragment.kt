import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.hbv601.folf.databinding.FragmentCoursesBinding
import com.hbv601.folf.CourseViewModel

class CourseFragment : Fragment() {

    private var _binding: FragmentCoursesBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: CourseViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursesBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(CourseViewModel::class.java)
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
        viewModel.fetchCourses()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}