package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.appcompat.app.ActionBar.DISPLAY_SHOW_CUSTOM
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.adapters.GameBoardArrayAdapter
import com.example.myapplication.adapters.LevelArrayAdapter
import com.example.myapplication.events.GameOverListener
import com.example.myapplication.events.MatchCardsListener
import com.example.myapplication.managers.MemoryGame
import com.example.myapplication.models.Level

class MainActivity : AppCompatActivity() {
    private var selectedLevel = Level.LOW
    set(value) {
        if(selectedLevel == value) return
        field = value
        restartGame(value)
        timer?.cancel()
        findViewById<LinearLayout>(R.id.match_container).visibility = View.GONE
        findViewById<TextView>(R.id.tv_time).text = ""
        remainingTime = 0
        val tvStart =  findViewById<TextView>(R.id.restart_level)
        tvStart.setText(R.string.start)
        tvStart.visibility = View.VISIBLE
    }

    private var remainingTime = 0L

    private var timer: CountDownTimer? = null

    private lateinit var memoryGame: MemoryGame

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.displayOptions = DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.title)
        initLevelSpinner()
        setEventForRestartLevel()
        buildCartSelection(selectedLevel)
    }

    override fun onResume() {
        if (remainingTime > 0)
         startCountDownTimer()
        super.onResume()
    }

    override fun onPause() {
        timer?.cancel()
        super.onPause()
    }

    private fun buildCartSelection(level: Level) {

       memoryGame = MemoryGame(this,level)

       memoryGame.onGameOverListener = object : GameOverListener {

            override fun onEnd() {
                this@MainActivity.finishGame( true)
            }
        }

        memoryGame.onMatchCardsListener = object : MatchCardsListener {
            override fun onMatch(resourceId: Int) {
                findViewById<TextView>(R.id.matches_counter).text = "${memoryGame.getMatches().size}"
            }
        }

       val lv = findViewById<ListView>(R.id.game_board)

       val adapter = GameBoardArrayAdapter(this, memoryGame)

        lv.adapter = adapter
    }

    private fun finishGame( isWin: Boolean) {

        val lv = findViewById<ListView>(R.id.game_board)
        timer?.cancel()
        remainingTime = 0

        val tvWin = findViewById<TextView>(R.id.tv_win)
        tvWin.visibility=View.VISIBLE

        if(isWin) {
            tvWin.setText(R.string.game_won)
            val zoomInAnimation =  AnimationUtils.loadAnimation(this, R.anim.zoom_in)
            tvWin.startAnimation(zoomInAnimation)
        }
        else {
            tvWin.setText(R.string.game_lost)
        }
        findViewById<LinearLayout>(R.id.container_won).visibility = View.VISIBLE
        val tvRestartLevel = findViewById<TextView>(R.id.restart_level)
        tvRestartLevel.setText(R.string.restart_level)
        tvRestartLevel.visibility = View.VISIBLE

        memoryGame.revealAllCards()

        lv.adapter = GameBoardArrayAdapter(this, memoryGame)
    }

    private fun initLevelSpinner() {
        val spinner = findViewById<Spinner>(R.id.level)
        spinner.adapter = LevelArrayAdapter(this)

        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            @Suppress("NAME_SHADOWING")
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedLevel = spinner.selectedItem as Level
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
    }

    private fun restartGame(level: Level) {
        buildCartSelection(level)
        findViewById<TextView>(R.id.matches_counter).setText(R.string.zerostring)
        val tvWin = findViewById<TextView>(R.id.tv_win)
        tvWin.clearAnimation()
        findViewById<LinearLayout>(R.id.container_won).visibility = View.GONE
        findViewById<TextView>(R.id.restart_level).visibility = View.GONE
    }

    private fun setEventForRestartLevel(){
        val tvRestartLevel = findViewById<TextView>(R.id.restart_level)

        tvRestartLevel.setOnClickListener {
            it.visibility = View.GONE

            remainingTime = duration

            if((it as TextView).text == resources.getString(R.string.restart_level)){
                restartGame(selectedLevel)
            }
            startCountDownTimer()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun startCountDownTimer() {
        findViewById<LinearLayout>(R.id.match_container).visibility = View.VISIBLE
        timer =  object : CountDownTimer(remainingTime, 1000L) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                remainingTime = millisUntilFinished
                findViewById<TextView>(R.id.tv_time).text= "${100+ (millisUntilFinished/1000)}".substring(1)
            }
            override fun onFinish() {
                finishGame(false)
            }
        }
        timer?.start()

       MemoryGame.isGaming = true
    }

   companion object {
       var duration = 61 * 1000L
   }

}