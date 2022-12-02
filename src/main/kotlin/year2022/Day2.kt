package year2022


fun day2(data: List<String>): Int {
    val gameScores = GameScores()

    fun findWhatToPick(elfShape: Shape, desiredResult: GameResult): Shape = when (desiredResult) {
        GameResult.WIN -> with(gameScores) { elfShape.looseAgainst() }
        GameResult.DRAW -> elfShape
        GameResult.LOOSE -> with(gameScores) { elfShape.winAgainst() }
    }

    val sum = data.sumOf { line ->
        val (elfChoice, desiredResult) = line.split(" ").let { (elfLetter, desiredResultLetter) ->
            Pair(ShapeFromLetter(elfLetter), GameResultFromLetter(desiredResultLetter))
        }
        val yourChoice = findWhatToPick(elfChoice, desiredResult)
        println("elf: $elfChoice, desiredResult: $desiredResult, yourChoice: $yourChoice")
        val scoreForResult = desiredResult.value
        val scoreForShape = yourChoice.value
        scoreForResult + scoreForShape
    }

    return sum
}

enum class Shape(val value: Int) {
    ROCK(1),
    PAPER(2),
    SCISSORS(3)
}

fun GameResultFromLetter(letter: String) = when (letter) {
    "X" -> GameResult.LOOSE
    "Y" -> GameResult.DRAW
    "Z" -> GameResult.WIN
    else -> throw IllegalStateException()
}

fun ShapeFromLetter(letter: String) = when (letter) {
    "A", "X" -> Shape.ROCK
    "B", "Y" -> Shape.PAPER
    "C", "Z" -> Shape.SCISSORS
    else -> throw IllegalStateException()
}

enum class GameResult(val value: Int) {
    WIN(6),
    DRAW(3),
    LOOSE(0)
}

class GameScores {

    private val data = listOf(Shape.ROCK, Shape.PAPER, Shape.SCISSORS)

    fun Shape.looseAgainst(): Shape {
        val indexOfThis = data.indexOf(this)
        return if (indexOfThis == data.lastIndex) data.first() else data[indexOfThis + 1]
    }

    fun Shape.winAgainst(): Shape {
        val indexOfThis = data.indexOf(this)
        return if (indexOfThis == 0) data.last() else data[indexOfThis - 1]
    }

    fun Shape.fightAgainst(other: Shape): GameResult = when (other) {
        this.looseAgainst() -> GameResult.LOOSE
        this.winAgainst() -> GameResult.WIN
        this -> GameResult.DRAW
        else -> throw IllegalStateException()
    }
}