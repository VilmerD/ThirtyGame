package se.lth.solid.vilmer.thirtygame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.res.ResourcesCompat
import java.io.File

class MainActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    // Constants
    private val numberDice: Int = 6
    private var CLICKED_COLOR: Int = 0
    private var UNCLICKED_COLOR: Int = 0

    // Game state variables
    private var numberRolls: Int = 1
    private val maxRolls: Int = 3
    private var numberSets: Int = 0
    private val maxSets: Int = 10
    private var scores: MutableList<Int> = mutableListOf()
    private var highScore: Int = 0

    // Views
    private lateinit var gameModel: GameModel
    private lateinit var rerollButton: Button
    private lateinit var buttonArray: MutableList<ToggleButton>
    private lateinit var spinner: Spinner
    private var option: String = "Low"
    private lateinit var resultsButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        CLICKED_COLOR = ResourcesCompat.getColor(resources, R.color.green, theme)
        UNCLICKED_COLOR = ResourcesCompat.getColor(resources, R.color.purple_700, theme)

        gameModel = GameModel(numberDice = numberDice)

        // Adding buttons
        buttonArray = addToggleButtons()
        setToggleButtonState(gameModel.getValues())

        spinner = findViewById(R.id.Spinner)
        ArrayAdapter.createFromResource(
            this,
            R.array.scrollOptions,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this

        rerollButton = findViewById(R.id.RerollButton)
        rerollButton.setOnClickListener { onRollButton() }

        resultsButton = findViewById(R.id.ResultsButton)
        resultsButton.setOnClickListener { onResultsButton() }
    }

    /**
     * Helper function for adding the buttons
     */
    private fun addToggleButtons(): MutableList<ToggleButton> {
        val buttons = mutableListOf<ToggleButton>()
        buttons.add(findViewById(R.id.ToggleButton1))
        buttons.add(findViewById(R.id.ToggleButton2))
        buttons.add(findViewById(R.id.ToggleButton3))
        buttons.add(findViewById(R.id.ToggleButton4))
        buttons.add(findViewById(R.id.ToggleButton5))
        buttons.add(findViewById(R.id.ToggleButton6))
        for (b in buttons) {
            b.setOnCheckedChangeListener { _, isChecked ->
                onToggleButton(b, isChecked)
            }
        }
        return buttons
    }

    /**
     * Formats the buttons for the new values
     */
    private fun setToggleButtonState(values: MutableList<Int>) {
        var vi: String
        var bi: ToggleButton
        for (i in 0 until numberDice) {
            vi = "" + values[i]
            bi = buttonArray[i]
            bi.textOff = vi
            bi.textOn = vi
            bi.isChecked = false
        }
    }

    /**
     * Generates an array of the buttons that are toggled, used for the game model
     */
    private fun getCheckedToggleButtons(): MutableList<Int> {
        val clicked = mutableListOf<Int>()
        for (i in 0 until numberDice) {
            if (buttonArray[i].isChecked)
                clicked.add(i)
        }
        return clicked
    }

    /**
     * Changes the color of the buttons to indicate if they're checked.
     */
    private fun onToggleButton(tb: ToggleButton, isChecked: Boolean) {
        if (isChecked && numberRolls < maxRolls) {
            tb.setBackgroundColor(CLICKED_COLOR)
        } else {
            tb.setBackgroundColor(UNCLICKED_COLOR)
        }
    }

    /**
     * Toggles the buttons clickable state
     */
    private fun setToggleButtonClickable(clickable: Boolean) {
        buttonArray.forEach { it.isClickable = clickable }
    }

    /**
     * On-Click function for the reroll button
     */
    private fun onRollButton() {
        if (numberRolls < maxRolls) {
            // The set is continued if the max-rolls has not been reached
            numberRolls++
            val clicked: MutableList<Int> = getCheckedToggleButtons()
            val newValues = gameModel.reroll(clicked)
            setToggleButtonState(newValues)
        } else {
            // else the score is evaluated and a new set is started
            numberSets++
            val x = if (option.contentEquals(charSequence = "Low")) 0 else option.toInt()
            val value = gameModel.computeResults(x)
            scores.add(value)

            setToggleButtonState(gameModel.reset())
            numberRolls = 1
            if (numberSets == maxSets) {
                highScore = highScore.coerceAtLeast(scores.sum())
                numberSets = 0
                val oldScores = scores
                scores = mutableListOf()
                displayResults(oldScores)
            }
        }
        setRollButtonState()
    }

    /**
     * Sets the text of the roll button and the state of the toggle buttons
     */
    private fun setRollButtonState() {
        if (numberRolls == maxRolls) {
            rerollButton.setText(R.string.rerollButtonEvaluate)
            setToggleButtonClickable(false)
        } else if (numberRolls == 1) {
            rerollButton.setText(R.string.rerollButtonDefault)
            setToggleButtonClickable(true)
        }
    }

    /**
     * On-Click function for the results button
     */
    private fun onResultsButton() {
        displayResults(scores)
    }

    /**
     * Helper function that displays results. Used for the results-button and when the game is done
     */
    private fun displayResults(showScores: MutableList<Int>) {
        val data = Intent(this, ResultsActivity::class.java).apply {
            putExtra(ResultsActivity.EXTRA_SCORES_KEY, showScores.toIntArray())
            putExtra(ResultsActivity.EXTRA_HIGH_SCORE_KEY, highScore)
        }
        startActivity(data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(ROLLS_KEY, numberRolls)
        outState.putIntArray(VALUES_KEY, gameModel.getValues().toIntArray())
        outState.putIntArray(SCORES_KEY, scores.toIntArray())
        outState.putInt(HIGH_SCORE_KEY, highScore)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        numberRolls = savedInstanceState.getInt(ROLLS_KEY)
        val savedValues: IntArray? = savedInstanceState.getIntArray(VALUES_KEY)
        if (savedValues != null) {
            val valuesList = savedValues.toMutableList()
            gameModel.setGameState(valuesList, numberRolls)
            setToggleButtonState(valuesList)
        }
        val savedScores: IntArray? = savedInstanceState.getIntArray(SCORES_KEY)
        if (savedScores != null) {
            scores = savedScores.toMutableList()
            numberSets = scores.size
        }
        highScore = savedInstanceState.getInt(HIGH_SCORE_KEY)
        setRollButtonState()
    }

    /**
     * On-Item function for the scroller
     */
    override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
        option = parent.getItemAtPosition(position).toString()
    }

    /**
     * Base case for scroller
     */
    override fun onNothingSelected(parent: AdapterView<*>?) {
        option = "Low"
    }

    companion object KEYS {
        const val VALUES_KEY = "EXTRA_VALUES"
        const val ROLLS_KEY = "EXTRA_ROLLS"
        const val SCORES_KEY = "EXTRA_SCORES"
        const val HIGH_SCORE_KEY = "EXTRA_HIGH_SCORE"
    }
}