package com.hbv601.folf.ViewHolders

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.HoleData
import com.hbv601.folf.Entities.PlayerEntity
import com.hbv601.folf.Entities.ScoreData
import com.hbv601.folf.databinding.ItemPlayerScoreBinding
import com.hbv601.folf.databinding.PostedScoreItemBinding

class PlayerScoreViewHolder(private val binding:ItemPlayerScoreBinding):RecyclerView.ViewHolder(binding.root) {
    val playerName = binding.textViewPlayerName
    val currentHoleDisp = binding.CurrentHoleNumber
    var currentHolenum = 1
    val currentStrokes = binding.editTextScore
    var maxHole = 1
    val currentParDisp = binding.ParNumber
    val postedScoreList = binding.PostedScores
    val postScore = binding.buttonSubmitScore
    val holeMap:MutableMap<Int,HoleData> = mutableMapOf()
    lateinit var player:PlayerEntity
    //scoreMap er mappað á id tölur holana á þeim forsendum að engin hola megi vera tvískráð
    private val scoreMap:MutableMap<Long,ScoreData> = mutableMapOf()
    val newScore = true
    lateinit var currentScoreData:ScoreData


    fun onBind(
        player:PlayerEntity,
        holes: List<HoleData>,
        scores: List<ScoreData>?){
        Log.d("holes",holes.toString())
        Log.d("player",player.toString())
        playerName.text = player.name
        for(hole in holes){
            holeMap[hole.hole_number] = hole
            if(hole.hole_number>maxHole){
                maxHole = hole.hole_number
            }
        }
        if(scores!=null){
            for(score in scores){
                scoreMap[score.hole_id] = score
            }
        }
        this.player = player
        var scorePresent = true
        while(scorePresent){
            if(scoreMap[holeMap[currentHolenum]!!.id.toLong()]==null) {scorePresent = false}
            else {updateHoleNum()}
        }
        currentParDisp.text = holeMap[currentHolenum]!!.par.toString()
        currentHoleDisp.text = currentHolenum.toString()
        newCurrentScore()
    }
    fun refreshScoreList(){
        var i = 1
        val scoreArray = mutableListOf<PostedScoreItem>()
        while(i>=maxHole){
            if(scoreMap[holeMap[i]!!.id.toLong()]!=null){
                scoreArray.add(PostedScoreItem(i,holeMap[i]!!.par,scoreMap[holeMap[i]!!.id.toLong()]!!.strokes.toInt()))
            }
            i++;
        }
        binding.PostedScores.removeAllViews()
        binding.PostedScores.adapter = postedScoreAdapter(scoreArray.toTypedArray()) { holeNum ->
            setSpecificHoleNum(holeNum) }
    }
    fun newCurrentScore(){
        currentScoreData = ScoreData(null,0,
            player.id!!,holeMap[currentHolenum]!!.id.toLong(),player.game_id,null)
        scoreMap[holeMap[currentHolenum]!!.id.toLong()] = currentScoreData
    }
    fun updateCurrentScore(){
        val currentScore = scoreMap[holeMap[currentHolenum]!!.id.toLong()]
        if (currentScore != null) {
            currentScoreData = ScoreData(currentScore.id,binding.editTextScore.text.toString().toLong(),
                player.id!!,currentScore.hole_id,currentScore.game_id,currentScore.timestamp)
        }
        refreshScoreList()

    }
    fun holesDone(){}
    fun updateHoleNum(){
        if(currentHolenum>=maxHole){
            holesDone()
        }
        currentHolenum++
        currentHoleDisp.text = currentHolenum.toString()
        if(holeMap[currentHolenum]!=null) {
            currentParDisp.text = holeMap[currentHolenum]!!.par.toString()
        }
    }
    fun setSpecificHoleNum(holeNum:Int){
        if(holeNum>=maxHole || holeNum<=0){
            return
        }
        currentHolenum = holeNum
        currentHoleDisp.text = currentHolenum.toString()
        currentParDisp.text = holeMap[currentHolenum]!!.par.toString()
        if(scoreMap[holeMap[currentHolenum]!!.id.toLong()]!=null){
            currentScoreData = scoreMap[holeMap[currentHolenum]!!.id.toLong()]!!
            currentStrokes.setText(currentScoreData.strokes.toInt())
        }

    }
    fun addScoreData(scoreData: ScoreData){
        scoreMap[scoreData.hole_id] = scoreData
        Log.d("playerScoreViewHolder",scoreData.toString())
        Log.d("playerScoreThrows",this.currentScoreData.toString())
        refreshScoreList()

    }

}
data class PostedScoreItem(val holeNum: Int,val par: Int, val strokes:Int)
class postedScoreAdapter(private val scores:Array<PostedScoreItem>,val onClick:(Int)->Unit):
    RecyclerView.Adapter<postedScoreAdapter.ScoreHolder>() {

    class ScoreHolder(val binding: PostedScoreItemBinding,val onClick: (Int) -> Unit):
        RecyclerView.ViewHolder(binding.root){
            private val parnum = binding.parNum
            private val holeNum = binding.HoleNum
            private val strokes = binding.KastTala
            private val skor = binding.scoreNum
            private var par: Int?=null

            init{
                binding.root.setOnClickListener{
                    par?.let{
                        onClick(it)
                    }
                }
            }
            fun bind(get:PostedScoreItem){
                par =get.par
                holeNum.text = get.holeNum.toString()
                parnum.text = get.par.toString()
                strokes.text = get.strokes.toString()
                skor.text = (get.par - get.strokes).toString()
            }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreHolder {
        val binding = PostedScoreItemBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        return ScoreHolder(binding,onClick)
    }

    override fun getItemCount(): Int {
        return scores.size
    }

    override fun onBindViewHolder(holder: ScoreHolder, position: Int) {
        holder.bind(scores[position])
    }
}