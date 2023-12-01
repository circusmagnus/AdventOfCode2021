package year2021

import kotlin.math.absoluteValue

fun day7(input: List<String>): Int {

    val positions = getPositions(input)
    val average = (positions.minOrNull()!! + positions.maxOrNull()!!) / 2
//    val mediana = positions.sorted()[positions.lastIndex / 2]
//    println("average: $average")

//    val totalFuel = positions.sumOf { position -> estimateFuel(position, average) }

//    private tailrec fun estimateFuel(index: Int): Int {
//        return if(index == )
//    }

    return estimateBestFuel(positions)
}

private fun estimateBestFuel(positions: List<Int>): Int {
    val sortedPositions = positions.sorted()
    val toCheckSorted = generateSequence(sortedPositions.first()) { position -> position + 1 }.take(sortedPositions.last())

    tailrec fun findMin(toCheckSorted: List<Int>, currentMin: Int): Int {
        if (toCheckSorted.isEmpty()) return currentMin
        val perhapsBestPosition = toCheckSorted.first()
        val totalFuel = sortedPositions.sumOf { position -> estimateFuel(position, perhapsBestPosition) }
        println("total fuel for position $perhapsBestPosition is: $totalFuel")

        return if (totalFuel > currentMin) return currentMin
        else findMin(toCheckSorted.drop(1), totalFuel)
    }

    return findMin(toCheckSorted.toList(), Int.MAX_VALUE)
}

private fun getPositions(data: List<String>) = data.first().split(",").map { it.toInt() }

private fun estimateFuel(position: Int, target: Int): Int {
    val totalDistance = (target - position).absoluteValue

    tailrec fun count(distanceMoved: Int, fuelBurned: Int): Int =
        if (distanceMoved == totalDistance) fuelBurned
        else {
            val newDistance = distanceMoved + 1
            val newFuel = fuelBurned + newDistance
            count(newDistance, newFuel)
        }

    return count(0, 0)
}

