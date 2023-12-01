package year2023

fun day1(input: List<String>): Int {
    val numbers = input.map { it.toNumber() }
    println("Got numbers: $numbers")

    return numbers.sum()
}

private fun String.toNumber(): Int {
    val first = findFirstDigit(this, "")
    val last = findLastDigit(this, "")
    return ("$first$last").toInt()
}

private const val ONE = "one"
private const val TWO = "two"
private const val THREE = "three"
private const val FOUR = "four"
private const val FIVE = "five"
private const val SIX = "six"
private const val SEVEN = "seven"
private const val EIGHT = "eight"
private const val NINE = "nine"

private fun String.digitOrNull() = when {
    this.lastOrNull()?.isDigit() ?: false -> this.last().digitToInt()
    this.firstOrNull()?.isDigit() ?: false -> this.first().digitToInt()
    this.contains(ONE) -> 1
    this.contains(TWO) -> 2
    this.contains(THREE) -> 3
    this.contains(FOUR) -> 4
    this.contains(FIVE) -> 5
    this.contains(SIX) -> 6
    this.contains(SEVEN) -> 7
    this.contains(EIGHT) -> 8
    this.contains(NINE) -> 9
    else -> null
}

private tailrec fun findFirstDigit(lettersLeft: String, accumulatedLetters: String): Int {
    val newAccumulated = accumulatedLetters + lettersLeft.first()
    return newAccumulated.digitOrNull() ?: findFirstDigit(lettersLeft.drop(1), newAccumulated)

}

private tailrec fun findLastDigit(lettersLeft: String, accumulatedLetters: String): Int {
//    println("lettersLeft: $lettersLeft, accumulated: $accumulatedLetters")
    val newAccumulated = lettersLeft.last() + accumulatedLetters
    return newAccumulated.digitOrNull() ?: findLastDigit(lettersLeft.dropLast(1), newAccumulated)
}