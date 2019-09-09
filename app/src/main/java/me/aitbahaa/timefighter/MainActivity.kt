package me.aitbahaa.timefighter

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    internal lateinit var tapMeButton: Button
    internal lateinit var gameScoreTextView: TextView
    internal lateinit var timeLeftTextView: TextView

    internal var score = 0
    internal var gameStarted = false
    internal lateinit var countDownTimer: CountDownTimer
    internal val initialCountDown: Long = 60_000
    internal val countDownInterval: Long = 1_000
    internal val TAG = MainActivity::class.java.simpleName
    internal var timeLeftOnTimer : Long = 60_000

    companion object{
        private val SCORE_KEY = "SCORE_KEY"
        private val TIME_LEFT_KEY = "TIME_LEFT_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, "onCreate caleld. score is: $score")

        tapMeButton = findViewById<Button>(R.id.tap_me_button)
        gameScoreTextView = findViewById<TextView>(R.id.game_score_text_view)
        timeLeftTextView = findViewById<TextView>(R.id.time_left_text_view)

        tapMeButton.setOnClickListener {
            view ->
            val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
            view.startAnimation(bounceAnimation)
            incrementScore()
        }

        if(savedInstanceState!=null){
            score = savedInstanceState.getInt(SCORE_KEY)
            timeLeftOnTimer = savedInstanceState.getLong(TIME_LEFT_KEY)
            restoreGame()
        }else{
            resetGame()
        }
    }

    private fun restoreGame() {
        updateScore()

        val restoredTime = timeLeftOnTimer/1_000
        updateTimeLeft(restoredTime)

        countDownTimer = object: CountDownTimer(timeLeftOnTimer, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1_000
                updateTimeLeft(timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        startGame()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        outState.putInt(SCORE_KEY,score)
        outState.putLong(TIME_LEFT_KEY, timeLeftOnTimer)
        countDownTimer.cancel()

        Log.d(TAG, "onSaveInstanceState: Saving score: $score & Time Left: $timeLeftOnTimer")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy called.")
    }

    private fun resetGame(){
        score = 0
        updateScore()

        val initialTimeLeft = initialCountDown/1_000
        updateTimeLeft(initialTimeLeft)

        countDownTimer = object: CountDownTimer(initialCountDown, countDownInterval){
            override fun onTick(millisUntilFinished: Long) {
                timeLeftOnTimer = millisUntilFinished
                val timeLeft = millisUntilFinished / 1_000
                updateTimeLeft(timeLeft)
            }

            override fun onFinish() {
                endGame()
            }
        }

        gameStarted = false
    }

    private fun startGame() {
        countDownTimer.start()
        gameStarted = true
    }

    private fun endGame(){
        Toast.makeText(this, getString(R.string.game_over_message, score.toString()), Toast.LENGTH_LONG).show()
        resetGame()
    }

    private fun incrementScore() {
        if(!gameStarted){
            startGame()
        }
        score++
        updateScore()
        animateScoreTextView()
    }

    private fun animateScoreTextView() {
        val bounceAnimation = AnimationUtils.loadAnimation(gameScoreTextView.context, R.anim.blink)
        gameScoreTextView.startAnimation(bounceAnimation)
    }


    private fun updateScore() {
        gameScoreTextView.text = getString(R.string.your_score, score.toString())
    }

    private fun updateTimeLeft(timeLeft: Long) {
        timeLeftTextView.text = getString(R.string.time_left, timeLeft.toString())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_about){
            showInfo()
        }
        return true
    }

    private fun showInfo() {
        val dialogTitle = getString(R.string.about_title, BuildConfig.VERSION_NAME)
        val dialogMessage = getString(R.string.about_message)
        val builder = AlertDialog.Builder(this)
        builder.setTitle(dialogTitle)
        builder.setMessage(dialogMessage)
        builder.create().show()
    }

}
