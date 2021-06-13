package se.lth.solid.vilmer.thirtygame

fun main() {
    testUntil()
}

fun testUntil() {
    for (i in 1 until 5) {
        println(i)
    }
}

fun testStringFormat() {
    println("Test joinToString")
    val gameModel = GameModel()
    val values = gameModel.getValues()
    val score = values.joinToString ("\n")
    val set = IntArray(10){ it + 1 }.joinToString("\n")
    println(score)
    println(set)
}

fun testAlgo() {
    println("Test Algo")
    val gameModel = GameModel()
    val values = gameModel.getValues()
    values.forEach {print(it)}
    println()
    println(gameModel.computeResults(x=2))
}

fun testReroll() {
    println("Test reroll")
    val gameModel = GameModel()
    val indices: MutableList<Int> = mutableListOf(0, 1, 2, 3)
    val values = gameModel.getValues()
    values.forEach {print(it)}
    println()
    val newValues = gameModel.reroll(indices)
    newValues.forEach {print(it)}
    println()
}

fun testReset() {
    println("Test reset")
    val gameModel = GameModel()
    val values = gameModel.getValues()
    values.forEach {print(it)}
    println()
    val newValues = gameModel.reset()
    newValues.forEach {print(it)}
    println()
}