package se.lth.solid.vilmer.thirtygame

import android.os.Parcelable
import kotlin.random.Random
import android.os.Parcel
import android.util.Log

/**
 * This class is a model of the thirty-game
 */
class GameModel : Parcelable {
    private val numberDice = 6
    private var dice: Array<Die> = Array(numberDice) { Die() }
    var choices: MutableList<Int> = mutableListOf()
    var scores: MutableList<Int> = mutableListOf()
    var numberRolls: Int = 0
    var numberSets: Int = 0

    private val TAG = "GameModel"

    /**
     * Rerolls a set of the dice
     * @param indices the indices of the dice to be rerolled
     */
    fun reroll(indices: MutableList<Int>) {
        Log.d(TAG, "In reroll")
        if (numberRolls < 2) {
            indices.forEach { dice[it].roll() }
            numberRolls++
        }
    }

    /**
     * Rerolls all dice
     */
    fun reset() {
        Log.d(TAG, "In reset")
        dice.forEach { it.roll() }
        numberRolls = 0
    }

    /**
     * Gets the values of the dice
     */
    fun getValues(): IntArray {
        return IntArray(numberDice) { i -> dice[i].value }
    }

    /**
     * Computes the value of the current die collection
     * @param x the sum to check against, see thirty-game rules
     */
    fun computeResults(x: Int): Int {
        Log.d(TAG, "In compute results")
        val values = getValues().toMutableList()
        val score = if (x > 3) {
            // Sorting in ascending order guarantees that the most dice-efficient combinations are
            // found
            values.sortDescending()
            values.reverse()
            findGroupsRecursively(0, values, x)
        } else {
            values.filter { it <= 3 }.sum()
        }
        choices.add(x)
        scores.add(score)
        numberSets++
        return score
    }

    /**
     * Recursive helper function for the results-algorithm
     * @param s sum of group members excluding next member
     * @param n the list of candidate number
     * @param x the sum to check against, see thirty-game rules
     */
    private fun findGroupsRecursively(s: Int, n: MutableList<Int>, x: Int): Int {
        // The sum of the remaining dice.
        val nSum: Int = n.sum()

        // "Base" cases
        if (s == x) {
            // If s sums to x, continue looking for combinations among the remaining dice
            return x + findGroupsRecursively(0, n, x)
        } else if (n.isEmpty() || s > x || s + nSum < x) {
            // Else, if no dice remain, or s exceeds x, or nSum + s is less than x no possible
            // combinations can be found and the algorithm terminates
            return 0
        }

        // The total number of achievable points. The result is often less than this since it is not
        // always possible to find a pairing. It is used to check if the algorithm is done or not
        val tMax = s + nSum - (s + nSum).rem(x)

        // Allocating memory
        var total = 0
        var ni: Int
        var nTemp: MutableList<Int>

        // One element at the time is removed from the list until a sum of x is found
        for (i in 0..n.lastIndex) {
            nTemp = n.toMutableList()
            ni = nTemp.removeAt(i)

            // Either a better score is found at the next call of findGroupsRecursively, in which
            // case total is updated, or the score at the next call is worse in which case total is
            // kept as is.
            val nextScore = findGroupsRecursively(s + ni, nTemp, x)
            total = total.coerceAtLeast(nextScore)

            // If total equals the max amount of achievable points the algorithm can stop
            // prematurely
            if (total == tMax) {
                return total
            }
        }
        return total
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(pout: Parcel, flags: Int) {
        Log.d(TAG, "In writeToParcel")
        pout.writeInt(numberSets)
        pout.writeInt(numberRolls)

        dice.forEach { pout.writeInt(it.value) }
        choices.forEach { pout.writeInt(it) }
        scores.forEach { pout.writeInt(it) }
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<GameModel> = object : Parcelable.Creator<GameModel> {
            override fun createFromParcel(pin: Parcel): GameModel {
                Log.d("GameModelCreator", "In createFromParcel")
                val gameModel = GameModel()
                gameModel.numberSets = pin.readInt()
                gameModel.numberRolls = pin.readInt()

                gameModel.dice.forEach { it.value = pin.readInt() }
                gameModel.choices = MutableList(gameModel.numberSets) { pin.readInt() }
                gameModel.scores = MutableList(gameModel.numberSets) { pin.readInt() }
                return gameModel
            }

            override fun newArray(size: Int): Array<GameModel> {
                return Array(size) { GameModel() }
            }
        }
    }
}

/**
 * A representation of a dice
 */
class Die(v: Int = Random.nextInt(1, 7)) {
    var value: Int = v

    /**
     * Rolls the die
     */
    fun roll() {
        value = Random.nextInt(1, 7)
    }
}