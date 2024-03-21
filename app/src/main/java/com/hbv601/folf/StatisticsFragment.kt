import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hbv601.folf.databinding.FragmentStatisticsBinding
import com.hbv601.folf.Entities.GameParcel

class StatisticsFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = FragmentStatisticsBinding.inflate(inflater, container, false)

        val args = arguments

        if (args != null && args.containsKey("GAME_PARCEL")) {
            val game: GameParcel? = args.getParcelable("GAME_PARCEL")

            //
            if (game != null) {
                //sækja og skrifa titil leiks
                val gameTitle = game.gameTitle
                if (!gameTitle.isNullOrEmpty()) {
                    binding.textViewGameTitle.text = gameTitle
                }

                //sækja og skrifa dagsetningu leiksins
                val gameTime = game.time
                if (!gameTime.isNullOrEmpty()) {
                    binding.textViewGameTime.text = gameTime
                }
            }
        }

        return binding.root
    }
}
