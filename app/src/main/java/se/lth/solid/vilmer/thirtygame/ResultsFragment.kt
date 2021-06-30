package se.lth.solid.vilmer.thirtygame

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import se.lth.solid.vilmer.thirtygame.databinding.FragmentResultsBinding

class ResultsFragment : Fragment() {
    private val gameViewModel: GameViewModel by activityViewModels()
    private lateinit var dataBinding: FragmentResultsBinding

    private val TAG = "ResultsFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "in onCreate")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d(TAG, "in onCreateView")
        dataBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_results,
            container, false
        )
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "in onViewCreated")
        super.onViewCreated(view, savedInstanceState)

        val scores = gameViewModel.getScores()
        val choices = gameViewModel.getChoices()
        Log.d(TAG, scores.toString())

        dataBinding.textViewScores.text = arrayToStackedString(scores)
        dataBinding.textViewChoices.text = choicesToString(choices)
        dataBinding.textViewSets.text = arrayToStackedString(IntArray(scores.size) { it + 1 })
        dataBinding.textViewTotalScore.text = ("" + scores.sum())

        dataBinding.newGameButton.setOnClickListener {
            gameViewModel.resetGame()
            activity?.onBackPressed()
        }
    }

    private fun arrayToStackedString(values: IntArray): String {
        return values.joinToString("\n ", " ")
    }

    private fun choicesToString(choices: IntArray): String {
        val sb: StringBuilder = StringBuilder(" ")
        choices.forEach {
            if (it == 0) {
                sb.append("Low\n ")
            } else {
                sb.append(("$it\n "))
            }
        }
        return sb.dropLast(2).toString()
    }
}