package se.lth.solid.vilmer.thirtygame

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import se.lth.solid.vilmer.thirtygame.databinding.FragmentGameBinding
import androidx.core.view.children
import java.io.FileNotFoundException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class GameFragment : Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentGameBinding
    private lateinit var buttons: MutableList<ToggleButton>
    private lateinit var adapter: ArrayAdapter<String>

    private val maxRolls = 2
    private var option = "Low"
    private val TAG = "GameFragment"

    private var colorOn: Int = 0
    private var colorOff: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        load()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)
        colorOn = ContextCompat.getColor(requireContext(), R.color.green)
        colorOff = ContextCompat.getColor(requireContext(), R.color.purple_700)

        addToggleButtons()
        dataBinding.RerollButton.setOnClickListener { onRollButton() }

        if (gameViewModel.choices.isEmpty()) {
            resetGame()
        }

        setRollButtonState(gameViewModel.getNumberRerolls())

        val spinner = dataBinding.Spinner
        adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            gameViewModel.choices
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                option = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        dataBinding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.reset_game -> {
                    resetGame()
                    true
                }
                R.id.high_scores -> {
                    findNavController().navigate(R.id.action_gameFragment_to_highScoresFragment)
                    true
                }
                else -> false
            }
        }

        return dataBinding.root
    }

    private fun resetGame() {
        gameViewModel.resetGame()
        setButtons()
        adapter.clear()
        adapter.addAll(gameViewModel.choices)
    }

    /**
     * On-Click function for the reroll button
     */
    private fun onRollButton() {
        Log.d(TAG, "in onRollButton")
        val numberRerolls = gameViewModel.getNumberRerolls()
        if (numberRerolls < maxRolls) {
            // The set is continued if the max-rolls has not been reached

            gameViewModel.reroll(getCheckedToggleButtons())
        } else {
            // else the score is evaluated and a new set is started

            gameViewModel.evaluateAndNewSet(option)
            adapter.remove(option)

            // if the game is finished the score screen is shown
            if (gameViewModel.choices.isEmpty()) {
                this.findNavController().navigate(R.id.action_gameFragment_to_resultsFragment)
            } else {
                option = gameViewModel.choices[0]
                dataBinding.Spinner.setSelection(0)
            }
        }
        setButtons()
        setRollButtonState(numberRerolls + 1)
    }

    /**
     * Sets the text of the roll button and the state of the toggle buttons
     */
    private fun setRollButtonState(numberRolls: Int) {
        Log.d(TAG, "in setRollButtonState")
        if (numberRolls == maxRolls) {
            dataBinding.RerollButton.setText(R.string.rerollButtonEvaluate)
            buttons.forEach { it.isClickable = false }
        } else {
            dataBinding.RerollButton.setText(R.string.rerollButtonDefault)
            buttons.forEach { it.isClickable = true }
        }
    }

    /**
     * Helper function for adding the buttons
     */
    private fun addToggleButtons() {
        buttons = mutableListOf()
        val gridLayout = dataBinding.grid
        gridLayout.children.forEach {
            val button = it as ToggleButton
            button.setOnCheckedChangeListener { _, isChecked ->
                button.setBackgroundColor(if (isChecked) colorOn else colorOff)
            }
            buttons.add(button)
        }
        setButtons()
    }

    /**
     * Generates an array of the buttons that are toggled, used for the game model
     */
    private fun getCheckedToggleButtons(): MutableList<Int> {
        Log.d(TAG, "in getCheckedToggleButtons")
        val clicked = mutableListOf<Int>()
        buttons.forEachIndexed { i, b -> if (b.isChecked) clicked.add(i) }
        return clicked
    }

    private fun setButtons() {
        Log.d(TAG, "in setButtons")
        gameViewModel.getValues().forEachIndexed {index: Int, v: Int ->
            buttons[index].textOn = "" + v
            buttons[index].textOff = "" + v
            buttons[index].isChecked = false
        }
    }

    private fun save() {
        try {
            val oos = ObjectOutputStream(
                requireContext().openFileOutput(
                    SAVE_FILE_NAME, Context.MODE_PRIVATE
                )
            )
            oos.reset()
            oos.writeObject(gameViewModel.highScores)
            oos.flush()
            oos.close()
        } catch (e: FileNotFoundException) {
            print(e.stackTrace)
        }
    }

    private fun load() {
        try {
            val ois = ObjectInputStream(requireContext().openFileInput(SAVE_FILE_NAME))
            gameViewModel.highScores = ois.readObject() as ArrayList<Score>
            ois.close()
        } catch (e: FileNotFoundException) {
            gameViewModel.highScores = arrayListOf()
            save()
        }
    }

    override fun onPause() {
        super.onPause()
        save()
    }

    companion object {
        const val SAVE_FILE_NAME = "highScores.sr1"
    }
}