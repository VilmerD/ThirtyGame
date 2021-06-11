package se.lth.solid.vilmer.thirtygame
import kotlin.random.Random

/**
 * This class is a model of the thirty-game
 *
 * @param numberDice the number of dice used in the game, 6 is default
 */
class GameModel(private val numberDice: Int = 6) {
    private var dice = Array(size=numberDice) {Die()}

    /**
     * Rerolls a set of the dice
     *
     * @param indices the indices of the dice to be rerolled
     */
    fun reroll(indices: IntArray) {
        for (i in indices) {
            dice[i].roll()
        }
    }

    /**
     * Rerolls all dice
     */
    fun reset() {
        for (i in 1..numberDice) {
            dice[i].roll()
        }
    }

    /**
     * Returns an array of the values of the dice
     */
    fun getValues() : IntArray {
        return IntArray(numberDice) {
                i -> dice[i].getValue()
        }
    }

    /**
     * Computes the value of the current die collection
     */
    fun computeResults(value: Int) : Int {
        TODO(reason = "Algorithm not complete")
    }
}

/**
 * A representation of a dice
 *
 * @param sides the number of sides of the die, 6 is default
 */
class Die(private val sides: Int = 6) {
    private var value: Int = Random.nextInt(from=1, sides)

    /**
     * Rolls the die
     */
    fun roll() {
        value = Random.nextInt(from=1, sides)
    }

    /**
     * Gets the value of the dice
     */
    fun getValue() : Int {
        return value
    }
}