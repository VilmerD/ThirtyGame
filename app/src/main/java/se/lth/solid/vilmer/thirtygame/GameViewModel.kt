package se.lth.solid.vilmer.thirtygame

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

class GameViewModel(private val handle: SavedStateHandle) : ViewModel() {
    private var game: GameModel
    var choices: MutableList<String>
    private val TAG = "GameViewModel"

    init {
        game = if (handle.contains("game")) {
            handle.get<GameModel>("game")!!
        } else {
            GameModel()
        }
        choices = if (handle.contains("choices")) {
            handle.get<MutableList<String>>("choices")!!
        } else {
            resetChoices()
        }
    }

    private fun resetChoices() : MutableList<String>{
        return mutableListOf("Low", "4", "5", "6", "7", "8", "9", "10", "11", "12")
    }

    fun getValues(): IntArray {
        Log.d(TAG, "in getValues")
        handle.set("game", game)
        return game.getValues()
    }

    fun getScores(): IntArray {
        Log.d(TAG, "in getScores")
        return game.scores.toIntArray()
    }

    fun getChoices(): IntArray {
        Log.d(TAG, "in getChoices")
        return game.choices.toIntArray()
    }

    fun getNumberRerolls(): Int {
        Log.d(TAG, "in getNumberRerolls")
        return game.numberRolls
    }

    fun reroll(buttons: MutableList<Int>) {
        Log.d(TAG, "in reroll with buttons:$buttons")
        game.reroll(buttons)
    }

    fun evaluateAndNewSet(choice: String) {
        Log.d(TAG, "in evaluateAndNewSet with x:$choice")
        choices.remove(choice)
        handle.set("choices", choices)

        val x = if (choice.contentEquals("Low")) 0 else choice.toInt()
        game.computeResults(x)
        game.reset()
    }

    fun resetGame() {
        Log.d(TAG, "in resetGame")
        game = GameModel()
        choices = resetChoices()
    }

}