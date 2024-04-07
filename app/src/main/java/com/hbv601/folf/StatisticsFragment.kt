import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.Entities.HoleData
import com.hbv601.folf.Entities.ScoreParcel
import com.hbv601.folf.R
import com.hbv601.folf.databinding.FragmentStatisticsBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StatisticsFragment : Fragment() {
    private var gameParcel: GameParcel? = null
    private var scoreParcel: ScoreParcel? = null
    private var holesList: List<HoleData>? = null
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            gameParcel = args.getParcelable("GAME_PARCEL")
        }

        gameParcel?.let { parcel ->
            val gameTitle = parcel.gameTitle
            val gameTime = parcel.time

            binding.textViewGameTitle.text = gameTitle
            binding.textViewGameTime.text = gameTime

            val fieldId = parcel.fieldId
            if (fieldId != null) {
                fetchHolesByFieldId(fieldId)
                fetchScoreByGameId(parcel.gameId)
            }
        }
    }

    private fun fetchHolesByFieldId(fieldId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                FolfApi.retrofitService.getHolesByFieldId(fieldId)
            }
            if (response.isSuccessful) {
                holesList = response.body()
                holesList?.let {
                    displayHoleData(it)
                }
            } else {
                Log.e("StatisticsFragment", "Failed to fetch holes for field ID: $fieldId")
            }
        }
    }

    private fun fetchScoreByGameId(gameId: Int) {
        GlobalScope.launch(Dispatchers.Main) {
            val response = withContext(Dispatchers.IO) {
                FolfApi.retrofitService.getScoreByGameId(gameId.toLong())
            }
            if (response.isSuccessful) {
                /*scoreParcel = response.body()
                scoreParcel?.let {
                    displayScoreData(it)
                }*/
            } else {
                Log.e("StatisticsFragment", "Failed to fetch score for game ID: $gameId")
            }
        }
    }

    private fun displayHoleData(holes: List<HoleData>) {
        val layoutInflater = LayoutInflater.from(context)
        val parentLayout = binding.layoutHoles

        for (hole in holes) {
            val view = layoutInflater.inflate(R.layout.item_hole_data, null)
            val textViewHoleNumber = view.findViewById<TextView>(R.id.textViewHoleNumber)
            val textViewPar = view.findViewById<TextView>(R.id.textViewPar)
            val textViewThrows = view.findViewById<TextView>(R.id.textViewThrows)

            textViewHoleNumber.text = "Hole ${hole.hole_number}:"
            textViewPar.text = "Par ${hole.par}"

            scoreParcel?.score?.let { scores ->
                val score = scores[hole.hole_number - 1]
                textViewThrows.text = "Your throws: $score"
            }

            parentLayout.addView(view)
        }
    }

    private fun displayScoreData(score: ScoreParcel) {
        this.scoreParcel = score
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}