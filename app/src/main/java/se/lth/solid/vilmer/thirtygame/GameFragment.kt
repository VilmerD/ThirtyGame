package se.lth.solid.vilmer.thirtygame

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ToggleButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import se.lth.solid.vilmer.thirtygame.databinding.FragmentGameBinding
import android.view.Menu

class GameFragment : Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentGameBinding
    private lateinit var buttons: MutableList<ToggleButton>
    private lateinit var adapter: ArrayAdapter<String>

    private val maxRolls = 2
    private var option = "Low"
    private val TAG = "GameFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_game,
            container, false
        )
        buttons = addToggleButtons()
        setButtons(gameViewModel.getValues())
        dataBinding.RerollButton.setOnClickListener {
            onRollButton()
        }

        if (gameViewModel.choices.isEmpty()) {
            gameViewModel.resetGame()
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

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
        return dataBinding.root
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
            adapter.notifyDataSetChanged()

            // if the game is finished the score screen is shown
            if (gameViewModel.choices.isEmpty()) {
                this.findNavController().navigate(R.id.action_gameFragment_to_resultsFragment)
            } else {
                option = gameViewModel.choices[0]
                dataBinding.Spinner.setSelection(0)
            }
        }
        setButtons(gameViewModel.getValues())
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
    private fun addToggleButtons(): MutableList<ToggleButton> {
        val buttons = mutableListOf<ToggleButton>()
        buttons.add(dataBinding.ToggleButton1)
        buttons.add(dataBinding.ToggleButton2)
        buttons.add(dataBinding.ToggleButton3)
        buttons.add(dataBinding.ToggleButton4)
        buttons.add(dataBinding.ToggleButton5)
        buttons.add(dataBinding.ToggleButton6)
        for (b in buttons) {
            b.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    b.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green))
                } else {
                    b.setBackgroundColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.purple_700
                        )
                    )
                }
            }
        }
        return buttons
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

    private fun setButtons(values: IntArray) {
        Log.d(TAG, "in setButtons")
        for (i in values.indices) {
            buttons[i].textOn = "" + values[i]
            buttons[i].textOff = "" + values[i]
            buttons[i].isChecked = false
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reset_game -> {
                gameViewModel.resetGame()
                true
            }
            R.id.high_scores -> {
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun save() {

    }

    private fun load() {

    }
}