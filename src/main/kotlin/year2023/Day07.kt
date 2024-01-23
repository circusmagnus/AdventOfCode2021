package year2023

import splitBy
import year2023.HandStrength.*

fun day7(input: List<String>): Long {
    val hands = input
        .map { entry -> entry.split(" ") }
        .onEach { println("Hand of: $it") }
        .map { (hand, bid) -> Hand(hand.toCharArray().asList(), bid.toInt()) }

    val orderedHands = hands.sorted()

    return orderedHands.mapIndexed { index, hand -> hand.bid * (index + 1) }.sum().toLong()

}
private val Char.cardValue: Int
    get() = if (this.isDigit()) digitToInt() else {
        when (this) {
            'T' -> 10
            'J' -> 1
            'Q' -> 12
            'K' -> 13
            'A' -> 14
            else -> throw IllegalArgumentException()
        }
    }

private data class Hand(val cards: List<Char>, val bid: Int): Comparable<Hand> {
    val strength = cards.asMap().values.toStrenght()
    init {
        require(cards.size == 5)
    }

    override fun compareTo(other: Hand): Int {
        val strengthComparison = this.strength.compareTo(other.strength)
        if (strengthComparison != 0) return strengthComparison

        tailrec fun cardValueCompare(remainingA: List<Char>, remainingB: List<Char>, lastResult: Int): Int {
            return if (lastResult != 0 || remainingA.isEmpty()) lastResult
            else {
                val result = remainingA.first().cardValue.compareTo(remainingB.first().cardValue)
                cardValueCompare(remainingA.drop(1), remainingB.drop(1), result)
            }
        }

        return cardValueCompare(this.cards, other.cards, lastResult = 0)
    }
}

enum class HandStrength {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIR,
    THREE,
    FULL_HOUSE,
    FOUR,
    FIVE
}

private fun List<Char>.asMap(): Map<Char, Int> {
    return buildMap {
        this@asMap.forEach { card ->
            val currCount = this[card] ?: 0
            this[card] = currCount + 1
        }

        val jokersCount = this['J'] ?: 0
        val mostNumerous = this.filter { it.key != 'J' }.maxByOrNull { it.value }
        mostNumerous?.let { numerous ->
            this['J'] = 0
            this[numerous.key] = numerous.value + jokersCount
        }
    }
}

private fun Collection<Int>.toStrenght(): HandStrength {
    return when {
        contains(5) -> FIVE
        contains(4) -> FOUR
        contains(3) && contains(2) -> FULL_HOUSE
        contains(3) -> THREE
        count { it == 2 } == 2 -> TWO_PAIR
        contains(2) -> ONE_PAIR
        else -> HIGH_CARD
    }
}