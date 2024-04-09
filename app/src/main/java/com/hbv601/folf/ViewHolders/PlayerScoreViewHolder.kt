package com.hbv601.folf.ViewHolders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hbv601.folf.Entities.HoleData
import com.hbv601.folf.Entities.PlayerEntity
import com.hbv601.folf.Entities.ScoreData
import com.hbv601.folf.databinding.ItemPlayerScoreBinding
import com.hbv601.folf.databinding.PostedScoreItemBinding

class PlayerScoreViewHolder(private val binding:ItemPlayerScoreBinding):RecyclerView.ViewHolder(binding.root) {
    val currentHoleDisp = binding.CurrentHoleNumber
    var currentHolenum = 1
    var maxHole = 0
    val currentParDisp = binding.ParNumber
    val postedScoreList = binding.PostedScores
    lateinit var holeMap:MutableMap<Int,HoleData>
    lateinit var player:PlayerEntity
    val scoreMap:MutableMap<Long,ScoreData> = mutableMapOf()
    val newScore = true
    lateinit var currentScoreData:ScoreData


    fun onBind(player:PlayerEntity,holes:ArrayList<HoleData>,scores:ArrayList<ScoreData>,postScore:(ScoreData)->ScoreData,updateScore:(ScoreData)->ScoreData){
        var holeMap = mutableMapOf<Int,HoleData>()
        for(hole in holes){
            holeMap[hole.hole_number] = hole
            if(hole.hole_number>maxHole){
                maxHole = hole.hole_number
            }
        }
        for(score in scores){
            scoreMap[score.hole_id] = score
        }
        this.player = player
        var scorePresent = true
        while(scorePresent){
            if(holeMap[currentHolenum]==null) scorePresent = false
            else updateHoleNum()
        }
        binding.buttonSubmitScore.setOnClickListener {
            if(!newScore){
                updateCurrentScore()
                val score = updateScore(currentScoreData)
                scoreMap[score.hole_id] = score
            }else{
                newCurrentScore()
                val score = postScore(currentScoreData)
                scoreMap[score.hole_id] = score
            }
        }


    }
    fun refreshScoreList(){
        var i = 1
        val scoreArray = mutableListOf<PostedScoreItem>()
        while(i>=maxHole){
            if(scoreMap[holeMap[i]!!.id.toLong()]!=null){
                scoreArray.add(PostedScoreItem(i,holeMap[i]!!.par,scoreMap[holeMap[i]!!.id.toLong()]!!.strokes.toInt()))
            }
        }

    }
    fun newCurrentScore(){
        currentScoreData = ScoreData(null,binding.editTextScore.text.toString().toLong(),
            player.id!!,holeMap[currentHolenum]!!.id.toLong(),player.game_id,null)
    }
    fun updateCurrentScore(){
        val currentScore = scoreMap[holeMap[currentHolenum]!!.id.toLong()]
        if (currentScore != null) {
            currentScoreData = ScoreData(currentScore.id,binding.editTextScore.text.toString().toLong(),
                player.id!!,currentScore.hole_id,currentScore.hole_id,currentScore.timestamp)
        }
    }
    fun holesDone(){}
    fun updateHoleNum(){
        if(currentHolenum>=maxHole){
            holesDone()
        }
        currentHolenum++
        currentHoleDisp.text = currentHolenum.toString()
        currentParDisp.text = holeMap[currentHolenum]!!.par.toString()
    }
    fun setSpecificHoleNum(holeNum:Int){
        if(holeNum>=maxHole || holeNum<=0){
            return
        }
        currentHolenum = holeNum
        currentHoleDisp.text = currentHolenum.toString()
        currentParDisp.text = holeMap[currentHolenum]!!.par.toString()

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
            private val par: Int?=null

            init{
                binding.root.setOnClickListener{
                    par?.let{
                        onClick(it)
                    }
                }
            }
            fun bind(get:PostedScoreItem){
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