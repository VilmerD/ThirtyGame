package se.lth.solid.vilmer.thirtygame

import kotlin.random.Random

/**
 * This class is a model of the thirty-game
 *
 * @param numberDice the number of dice used in the game, 6 is default
 */
class GameModel(private val numberDice: Int = 6) {
    private var dice = Array(size = numberDice) { Die() }
    private var numberRolls: Int = 0
    private val maxRolls: Int = 3

    /**
     * Rerolls a set of the dice
     * @param indices the indices of the dice to be rerolled
     */
    fun reroll(indices: MutableList<Int>): MutableList<Int> {
        if (indices.isNotEmpty() && numberRolls < maxRolls) {
            for (i in indices) {
                dice[i].roll()
            }
            numberRolls++
        }
        return getValues()
    }

    /**
     * Rerolls all dice
     */
    fun reset(): MutableList<Int> {
        for (i in 0 until numberDice) {
            dice[i].roll()
        }
        numberRolls = 0
        return getValues()
    }

    /**
     * Returns an array of the values of the dice
     */
    fun getValues(): MutableList<Int> {
        return MutableList(numberDice) { i -> dice[i].getValue() }
    }

    /**
     * Computes the value of the current die collection
     * @param x the sum to check against, see thirty-game rules
     */
    fun computeResults(x: Int): Int {
        val values: MutableList<Int> = getValues()
        return if (x > 3) {
            values.sortDescending()
            values.reverse()
            recursiveSum(s = 0, values, x)
        } else {
            values.filter{ it <= 3 }.sum()
        }
    }

    /**
     * Recursive helper function for the results-algorithm
     * @param s sum of group members excluding next member
     * @param n the list of candidate number
     * @param x the sum to check against, see thirty-game rules
     */
    private fun recursiveSum(s: Int, n: MutableList<Int>, x: Int): Int {
        // "Base" cases
        val nSum: Int = n.sum()
        if (s == x) {
            return x + recursiveSum(s=0, n, x)
        } else if (n.isEmpty() || s > x || s + nSum < x) {
            return 0
        }

        // The total number of achievable points
        val tMax = s + nSum - (s + nSum).rem(x)
        var total = 0
        var ni: Int
        var nTemp: MutableList<Int>

        // One element at the time is removed from the list until a sum of x is found
        for (i in 0..n.lastIndex) {
            nTemp = n.toMutableList()
            ni = nTemp.removeAt(i)
            total = total.coerceAtLeast(recursiveSum(s=s + ni, nTemp, x))

            // If total equals the max amount of achievable points the algorithm can stop
            // prematurely
            if (total == tMax) {
                return total
            }
        }
        return total
    }

    /**
     * Sets the game state to a previous state
     * @param values the previous values
     * @param rolls the previous number of rolls in current set
     */
    fun setGameState(values: MutableList<Int>, rolls: Int) {
        for (i in 0 until numberDice) {
            dice[i].setValue(values[i])
        }
        numberRolls = rolls
    }

}

/**
 * A representation of a dice
 *
 * @param sides the number of sides of the die, 6 is default
 */
class Die(private val sides: Int = 6) {
    private var value: Int = Random.nextInt(from = 1, sides)

    /**
     * Rolls the die
     */
    fun roll() {
        value = Random.nextInt(from = 1, until = sides + 1)
    }

    /**
     * Gets the value of the dice
     */
    fun getValue(): Int {
        return value
    }

    fun setValue(v: Int) {
        value = v
    }
}