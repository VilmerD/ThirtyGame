package se.lth.solid.vilmer.thirtygame

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultsActivity : AppCompatActivity() {
    // Views
    private lateinit var setsView: TextView
    private lateinit var scoresView: TextView
    private lateinit var totalScoreView: TextView
    private lateinit var highScoreView: TextView
    private lateinit var returnButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        // Buttons and views
        returnButton = findViewById(R.id.returnButton)
        returnButton.setOnClickListener { onReturnButton() }
        setsView = findViewById(R.id.textViewSets)
        scoresView = findViewById(R.id.textViewScores)
        totalScoreView = findViewById(R.id.textViewTotalScore)
        highScoreView = findViewById(R.id.textViewHighScore)

        // Getting data from Intent
        val values = intent.getIntArrayExtra(EXTRA_SCORES_KEY)
        val highScore = intent.getIntExtra(EXTRA_HIGH_SCORE_KEY, 0)
        if (values != null) {
            setSetText(values.size)
            setScoreText(values)
            setHighScoreText(highScore)
        }
    }

    private fun setHighScoreText(highScore: Int) {
        val s = "HS: $highScore"
        highScoreView.text = s
    }

    private fun onReturnButton() {
        setResult(RESULT_OK, Intent())
        finish()
    }

    private fun setSetText(numberSets: Int) {
        val values = IntArray(numberSets) { it + 1 }
        setsView.text = arrayToString(values)
    }

    private fun setScoreText(values: IntArray) {
        scoresView.text = arrayToString(values)
        totalScoreView.text = (" " + values.sum())
    }

    private fun arrayToString(values: IntArray): String {
        return values.joinToString(prefix = " ", separator = "\n ")
    }

    companion object KEYS {
        const val EXTRA_HIGH_SCORE_KEY: String = "EXTRA_HIGH_SCORE"
        const val EXTRA_SCORES_KEY: String = "EXTRA_SCORES"
    }
}