package year2023

import Position
import WithPosition

fun day3(input: List<String>): Int {
//    val allSymbols = input
//        .mapIndexed { yIndex, line -> getSymbols(line, yIndex) }
//        .flatten()
//    val symbolPositions = allSymbols.map { it.withAdjacent }.flatten().toSet()
//
//    val lineNumbers = input
//        .mapIndexed { yIndex, line -> getValidNumbers(line, yIndex, symbolPositions) }
//
//    return lineNumbers.sum()

    val numbers = input
        .mapIndexed { yIndex, line -> justGetNumbers(line, yIndex) }
        .flatten()

    val numbersMap = buildMap<Position, AdventNumber> {
        numbers.forEach { number ->
            number.positions.forEach { position ->
                put(position, number)
            }
        }
    }

//    println("numbers map: $numbersMap")

    val gears = input
        .mapIndexed { yIndex, line -> findGearRatios(line, yIndex, numbersMap) }
//        .sum()

    return gears.sum()
}

data class Symbol(override val position: Position, val value: Char, val withAdjacent: Set<Position>) : WithPosition
data class DigitPosition(override val position: Position, val value: Char) : WithPosition

data class AdventNumber(val value: Int, val positions: List<Position>)

private fun getSymbols(line: String, yIndex: Int): Set<Symbol> {
    val symbols = mutableSetOf<Symbol>()
    for (xIndex in 0..line.lastIndex) {
        val character = line[xIndex]
        if (character.isDigit().not() && character != '.') {
            val centerPos = Position(x = xIndex, y = yIndex)
            val topLeft = Position(x = xIndex - 1, y = yIndex - 1)
            val top = Position(x = xIndex, y = yIndex - 1)
            val topRight = Position(x = xIndex + 1, y = yIndex - 1)
            val right = Position(x = xIndex + 1, y = yIndex)
            val bottomRight = Position(x = xIndex + 1, y = yIndex + 1)
            val bottom = Position(x = xIndex, y = yIndex + 1)
            val bottomLeft = Position(x = xIndex - 1, y = yIndex + 1)
            val left = Position(x = xIndex - 1, y = yIndex)
            val symbol = Symbol(
                position = centerPos,
                value = character,
                withAdjacent = setOf(centerPos, topLeft, top, topRight, right, bottomRight, bottom, bottomLeft, left)
            )
            symbols.add(symbol)
        }
    }

    println("in line $yIndex found symbols: $symbols")

    return symbols
}

private fun justGetNumbers(line: String, yIndex: Int): List<AdventNumber> {
    val numbers = mutableListOf<AdventNumber>()
    val digits = mutableListOf<DigitPosition>()

    for (xIndex in 0..line.lastIndex) {
        val character = line[xIndex]
        when {
            character.isDigit() -> {
                val pos = Position(x = xIndex, y = yIndex)
                digits.add(DigitPosition(pos, value = character))
            }

            digits.isNotEmpty() -> {
                val value = digits.map { it.value }.joinToString(separator = "").toInt()
                numbers.add(AdventNumber(value = value, positions = digits.map { it.position }))
                digits.clear()
            }
        }
    }

    if (digits.isNotEmpty()) {
        val value = digits.map { it.value }.joinToString(separator = "").toInt()
        numbers.add(AdventNumber(value = value, positions = digits.map { it.position }))
        digits.clear()
    }
    return numbers
}

private fun findGearRatios(line: String, yIndex: Int, numbersMap: Map<Position, AdventNumber>): Int {
    var ratios = 0

    for (xIndex in 0..line.lastIndex) {
        val character = line[xIndex]

        if (character == '*') {
            ratios += findRatio(gearPos = Position(x = xIndex, y = yIndex), numbersMap)
        }
    }

    return ratios
}

private fun findRatio(gearPos: Position, numbersMap: Map<Position, AdventNumber>): Int {

//    println("finding gear ratios. gearPos: $gearPos")
    val adjacent = buildList<Position> {
        for (yMod in -1..1) {
            for (xMod in -1..1) {
                add(Position(x = gearPos.x + xMod, y = gearPos.y + yMod))
            }
        }
    }

    val adjacentNumbers = adjacent.mapNotNull { pos -> numbersMap[pos] }.toSet()

//    println("adjacent found: $adjacentNumbers")

    val ratio = if (adjacentNumbers.size == 2) {
        adjacentNumbers.first().value * adjacentNumbers.last().value
    } else 0

//    println("found ratio: $ratio")

    return ratio
}


private fun getValidNumbers(line: String, yIndex: Int, symbolPositions: Set<Position>): Int {
    val digits = mutableSetOf<DigitPosition>()
//    val numbers = mutableSetOf<SimpleNumber>()
    var lineSum = 0

//    val interestingSymbols = symbols.filter { symbol -> symbol.withAdjacent.any { it.y ==  } }

    for (xIndex in 0..line.lastIndex) {
        val character = line[xIndex]

        when {
            character.isDigit() -> {
                val pos = Position(x = xIndex, y = yIndex)
                digits.add(DigitPosition(pos, value = character))
            }

            digits.isNotEmpty() -> {
                lineSum += addNumberOrNot(digits, symbolPositions)
                digits.clear()
            }
        }
    }
    lineSum += addNumberOrNot(digits, symbolPositions)
    digits.clear()

//    println("in line $yIndex found numbers: $numbers")
    println("in line $yIndex lineSum: $lineSum")

    return lineSum
}

private fun addNumberOrNot(
    digits: Set<DigitPosition>,
    symbolPositions: Set<Position>,
//    numbers: MutableSet<SimpleNumber>
): Int {
    println("adding number or not. digits: $digits")
    val numberPos = digits.map { it.position }
    val newNumber = if (numberPos.any { it in symbolPositions }) {
//        val newNumber = SimpleNumber(digits.map { digit -> digit.value }.joinToString(separator = ""))
        val number = digits.map { it.value }.joinToString(separator = "").toInt()
//        numbers.add(newNumber)
        println("number added: $number")
        number
    } else 0

    return newNumber
}

//private fun getData(line: String, yIndex: Int): Pair<Set<Symbol>, Set<Number>> {
//
//    val symbols = mutableSetOf<Symbol>()
//    val digits = mutableSetOf<DigitPosition>()
//    val numbers = mutableSetOf<Number>()
//
//    for (xIndex in 0..line.lastIndex) {
//        val character = line[xIndex]
//        when {
//            character.isDigit() -> digits.add(DigitPosition(Position(x = xIndex, y = yIndex), value = character))
//            character == '.' ->
//        }
//    }
//}