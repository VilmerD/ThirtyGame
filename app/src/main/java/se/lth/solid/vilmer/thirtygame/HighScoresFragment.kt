package se.lth.solid.vilmer.thirtygame

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import se.lth.solid.vilmer.thirtygame.databinding.FragmentHighScoresBinding

class HighScoresFragment : Fragment() {

    private lateinit var viewBinding : FragmentHighScoresBinding

    private val gameViewModel: GameViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_high_scores, container, false)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val separator = "\n "
        val prefix = " "
        gameViewModel.highScores.sortWith { s: Score, t: Score -> t.score - s.score }
        val numberHighScores = gameViewModel.highScores.size
        val scores = Array(numberHighScores) { gameViewModel.highScores[it].score }
        val names = Array(numberHighScores) { gameViewModel.highScores[it].name }
        viewBinding.textViewHighScores.text = scores.joinToString(separator, prefix)
        viewBinding.textViewHighScoreNames.text = names.joinToString(separator, prefix)
    }
}