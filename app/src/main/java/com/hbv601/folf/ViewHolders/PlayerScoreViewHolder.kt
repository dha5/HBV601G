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
    val scoreList:ArrayList<PostedScoreItem> = arrayListOf()
    //map of hole_id ints to hole_number ints
    val holeIdMap:MutableMap<Int,Int> = mutableMapOf()
    lateinit var player:PlayerEntity
    //scoreMap er mappað á id tölur holana á þeim forsendum að engin hola megi vera tvískráð
    private val scoreMap:MutableMap<Int,ScoreData> = mutableMapOf()
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
            holeIdMap[hole.id] = hole.hole_number
            if(hole.hole_number>maxHole){
                maxHole = hole.hole_number
            }
        }
        var scorePresent = false
        if(scores!=null){

            scorePresent = true
            for(score in scores){
                addScore(score)
                val holeNum = holeIdMap[holeIdMap[score.hole_id.toInt()]]
                if(scorePresent && holeNum == currentHolenum){
                    if(currentHolenum<maxHole)currentHolenum++
                }else{
                    scorePresent = false
                }
            }
        }
        this.player = player
        binding.PostedScores.adapter = PostedScoreAdapter(scoreList) { holeNum ->
            setSpecificHoleNum(holeNum)
        }

        setCurrentScore(currentHolenum)

    }
    fun setCurrentScore(holeNum: Int){
        currentHolenum = holeNum
        if(scoreMap[holeMap[holeNum]!!.id]!=null){
            currentScoreData = scoreMap[holeMap[holeNum]!!.id]!!
        }else{
            currentScoreData = ScoreData(null,
                0,
                player.id!!,
                holeMap[holeNum]!!.id.toLong(),
                player.game_id,null)
        }
        Log.d("currentScoreData",currentScoreData.toString())
        currentParDisp.text = holeMap[holeNum]!!.par.toString()
        currentHoleDisp.text = holeNum.toString()
        currentStrokes.setText(currentScoreData.strokes.toString())
    }

    fun updateCurrentScore(){
        currentScoreData = ScoreData(currentScoreData.id,binding.editTextScore.text.toString().toLong(),
            player.id!!,currentScoreData.hole_id,currentScoreData.game_id,currentScoreData.timestamp)
    }
    fun addScore(scoreData: ScoreData) {
        val holeNum = holeIdMap[scoreData.hole_id.toInt()]
        if (holeNum != null) {
            scoreMap[scoreData.hole_id.toInt()] = scoreData
            for (score in scoreList) {
                if (score.holeNum == holeNum) {
                    scoreList.remove(score)
                }
            }
            scoreList.add(
                PostedScoreItem(
                    holeNum,
                    holeMap[holeNum]!!.par,
                    scoreData.strokes.toInt()
                )
            )
            binding.PostedScores.adapter =
                PostedScoreAdapter(scoreList) { hole -> setSpecificHoleNum(hole) }
        }
    }
    fun holesDone(){}
    fun nextHole(){
        while(currentHolenum<maxHole){
            if(scoreMap[holeMap[currentHolenum]?.id] !=null){
                currentHolenum++;
            }else
                break
        }
        setCurrentScore(currentHolenum)

    }
    fun setSpecificHoleNum(holeNum:Int) {
        if (holeNum >= maxHole || holeNum <= 0) {
            return
        }
        currentHolenum = holeNum
        currentHoleDisp.text = currentHolenum.toString()
        currentParDisp.text = holeMap[currentHolenum]!!.par.toString()
        if (scoreMap[holeMap[currentHolenum]!!.id] != null) {
            currentScoreData = scoreMap[holeMap[currentHolenum]!!.id]!!
            currentStrokes.setText(currentScoreData.strokes.toInt())
        }
    }

    @Deprecated("no longer used redirects to addScore",ReplaceWith("addScoreData"),DeprecationLevel.WARNING)
    fun addScoreData(scoreData: ScoreData){
        addScore(scoreData)
        nextHole()
    }

}

data class PostedScoreItem(val holeNum: Int,val par: Int, val strokes:Int)
class PostedScoreAdapter(private val scores:ArrayList<PostedScoreItem>,val onClick:(Int)->Unit):
    RecyclerView.Adapter<PostedScoreAdapter.ScoreHolder>() {

    class ScoreHolder(val binding: PostedScoreItemBinding,val onClick: (Int) -> Unit):
        RecyclerView.ViewHolder(binding.root){
            private val parnum = binding.parNum
            private val holeNum = binding.HoleNum
            private val strokes = binding.KastTala
            private val skor = binding.scoreNum
        fun bind(get:PostedScoreItem){
            binding.root.setOnClickListener{
                onClick(get.par)
            }
            holeNum.text = get.holeNum.toString()
            parnum.text = get.par.toString()
            strokes.text = get.strokes.toString()
            skor.text = (get.par - get.strokes).toString()
        }
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreHolder {
        val binding = PostedScoreItemBinding.inflate(LayoutInflater.from(parent.context))
        return ScoreHolder(binding,onClick)
    }

    override fun getItemCount(): Int {
        return scores.size
    }

    override fun onBindViewHolder(holder: ScoreHolder, position: Int) {
        holder.bind(scores[position])
    }
}