package com.hbv601.folf


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.CourseData
import com.hbv601.folf.Entities.GameData
import com.hbv601.folf.Entities.GameParcel
import com.hbv601.folf.Entities.HoleData
import com.hbv601.folf.Entities.PlayerEntity
import com.hbv601.folf.Entities.ScoreData
import com.hbv601.folf.Entities.ScoreParcel
import com.hbv601.folf.databinding.FragmentStatisticsBinding
import com.hbv601.folf.databinding.PostedScoreItemBinding
import com.hbv601.folf.network.FolfApi
import kotlinx.coroutines.launch

class StatisticsFragment : Fragment() {
    private var gameParcel: GameParcel? = null
    private var gameId: Int? = null
    private var fieldId: Int? = null
    private var scoreParcel: ScoreParcel? = null
    private var playerScores: MutableMap<Int, MutableList<ScoreData>> = mutableMapOf()
    private var holesList: List<HoleData>? = null
    private var holesMap: MutableMap<Long,HoleData> = mutableMapOf()
    private var field: CourseData? = null
    private var game: GameData? = null
    private var players: List<PlayerEntity>? = null
    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private var gameGet = false
    private var fieldGet = false
    private var showGame = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            gameId = args.getInt("GAME_ID")
            fieldId = args.getInt("FIELD_ID")
        }
        binding.homeButton.setOnClickListener {
            findNavController().navigate(R.id.action_statistics_to_homepage)
        }
        if(gameId!=null){
            lifecycleScope.launch {
                if(fieldId!=null){
                    getField(fieldId!!)
                }
                getGame(gameId!!)
                showScores()
            }

        }

        /*
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
        }*/

    }
    private fun scoreAwaiter(flag: Int){
        if(flag == 1){
            gameGet = true
        }else if(flag == 2){
            fieldGet = true
        }
        if(gameGet && fieldGet && showGame){
            showGame = false
            showScores()
        }
    }
    private suspend fun getField(fieldId: Int){
            val res = FolfApi.retrofitService.getFieldByFieldId(fieldId!!)
            Log.d("fieldResponse",res.toString())
            if(res.isSuccessful){
                field = res.body()
                val holeRes = FolfApi.retrofitService.getHolesByFieldId(fieldId!!)
                Log.d("holeresponse",holeRes.toString())
                if(holeRes.isSuccessful()){
                    holesList = holeRes.body()
                    for(hole in holesList!!){
                        holesMap[hole.id.toLong()] = hole
                    }
                    //scoreAwaiter(2)
                }
            }
    }
    private suspend fun getGame(gameId: Int){
            val res = FolfApi.retrofitService.getGameById(gameId.toLong())
            if(res.isSuccessful){
                game = res.body()
                Log.d("game",game.toString())
                if(fieldId!=null){
                    getField(game!!.field_id!!.toInt())
                }
                val resp = FolfApi.retrofitService.getScoreByGameId(gameId.toLong())
                Log.d("scoresresp",resp.toString())
                if(resp.isSuccessful){
                    val scores = resp.body()
                    if(scores!=null){
                        for(score in scores){
                            if(playerScores.contains(score.player_id.toInt())){
                                playerScores[score.player_id.toInt()]!!.add(score)
                            }else playerScores[score.player_id.toInt()] = mutableListOf(score)
                        }
                    }
                }
                val response = FolfApi.retrofitService.getGamePlayers(gameId.toLong())
                Log.d("playerresp",response.toString())
                if(response.isSuccessful){
                    players = response.body()
                }
                //scoreAwaiter(1)

            }

    }
    private fun showScores(){

        if(game!=null){
            val layoutInflater = LayoutInflater.from(context)
            val parentLayout = binding.layoutHoles
            binding.textViewGameTitle.text = game!!.name + " - ${field!!.name}"
            binding.textViewGameTime.text = game!!.date_created
            Log.d("players",players.toString())
            if(players!=null){
                for(player in players!!){
                    val scores = playerScores[player.id!!.toInt()]
                    Log.d("scores",scores.toString())
                    if(scores !=null){
                        val linearLayout =  LinearLayout(requireContext())
                        linearLayout.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
                        val playerTextView = TextView(requireContext())
                        playerTextView.text = player.name
                        playerTextView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
                        parentLayout.addView(playerTextView)
                        val holeList = mutableMapOf<Int,PastScoreViewHolder>()
                        var maxHole = 0
                        for(score in scores){
                            val hole = holesMap[score.hole_id]
                            val viewHolder = PastScoreViewHolder(PostedScoreItemBinding.inflate(layoutInflater))
                            viewHolder.onBind(hole!!.hole_number,hole.par,score.strokes.toInt())
                            Log.d("holeNum",hole.hole_number.toString())
                            holeList[hole!!.hole_number] = viewHolder
                            if(maxHole<hole!!.hole_number) maxHole = hole.hole_number
                        }
                        var i = 1
                        while(i<=maxHole){

                            if(holeList[i]!=null){
                                Log.d("scoreNum",i.toString())
                                parentLayout.addView(holeList[i]!!.itemView)
                            }
                            i++
                        }
                    }
                }

            }
        }

    }

    /*
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
                Log.e("com.hbv601.folf.StatisticsFragment", "Failed to fetch holes for field ID: $fieldId")
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
                Log.e("com.hbv601.folf.StatisticsFragment", "Failed to fetch score for game ID: $gameId")
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
    */


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
class PastScoreViewHolder(val binding:PostedScoreItemBinding):RecyclerView.ViewHolder(binding.root){
    fun onBind(hole:Int,par:Int,strokes:Int){
        binding.HoleNum.text = hole.toString()
        binding.parNum.text = par.toString()
        binding.KastTala.text = strokes.toString()
        binding.scoreNum.visibility = View.INVISIBLE
        binding.textView14.visibility = View.INVISIBLE
    }
}