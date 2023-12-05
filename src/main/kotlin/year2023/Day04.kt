package year2023

fun day4(input: List<String>): Int {
    val cards = input
        .map { line -> parseWinningAndCurrent(line) }

    val result = countAllWonCards(cards, score = cards.size)

    return result
}

private tailrec fun countAllWonCards(cardsLeft: List<Card>, score: Int): Int {
    println("all cards iteration, left: $cardsLeft, score: $score")
    if(cardsLeft.isEmpty()) return score

    val head = cardsLeft.first()
    val newScore = countWonCards(head, cardsLeft, score)

    return countAllWonCards(cardsLeft.drop(1), newScore)
}

private fun countWonCards(card: Card, allCards: List<Card>, currentScore: Int): Int {
    val winning = card.winningNumbers

    fun count(reachTo: Int, currentLeft: List<Int>, score: Int): Int {
        if(currentLeft.isEmpty()) return score

        val head = currentLeft.first()
        val (newScore, newReachTo) = if (head in winning) {
            val copy = (card.id + reachTo).let { copyId -> allCards.first { it.id == copyId } }
            val copyScore = countWonCards(copy, allCards, score + 1)
            Pair(copyScore, reachTo + 1)
        } else {
            Pair(score, reachTo)
        }

        return count(newReachTo, currentLeft.drop(1), newScore)
    }

    return count(reachTo = 1, currentLeft = card.currentNumbers, currentScore)
}

private fun countPoints(card: Card): Int {
    val winning = card.winningNumbers

    tailrec fun count(currentLeft: List<Int>, score: Int): Int {
        if (currentLeft.isEmpty()) return score

        val head = currentLeft.first()
        val newScore = if(head in winning) {
            if(score > 0) score * 2 else 1
        } else {
            score
        }

        return count(currentLeft.drop(1), newScore)
    }

    return count(card.currentNumbers, 0)
}

data class Card(val id: Int, val winning: List<String>, val current: List<String>) {
    val winningNumbers
        get() = winning.map { it.toInt() }

    val currentNumbers
        get() = current.map { it.toInt() }
}

private fun parseWinningAndCurrent(input: String): Card {
    val numbers = input.split(": ")
        .last()

    val winning = numbers
        .split('|')
        .first()
        .split(" ")
        .map { it.filter { it != ' ' } }
        .filter { it.isNotBlank() }

    val current = numbers
        .split('|')
        .last()
        .split(" ")
        .map { it.filter { it != ' ' } }
        .filter { it.isNotBlank() }

    val id = input.split(":")
        .first()
        .split(" ")
        .last()
        .toInt()


    val card = Card(id, winning, current)

    println(card)

    return card
}