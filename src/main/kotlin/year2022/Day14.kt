package year2022

import Position
import WithEightNeighbours
import initEightNeighbours
import year2022.FallResult.*
import year2022.Type.*

fun day14(data: List<String>): Int {
    val map = makeMap()
    map.initEightNeighbours()
    data.forEach { line -> line.applyIntructionTo(map) }
    val points = map.flatten()
    val maxY = points.filter { it.type == ROCK }.maxOf { pos -> pos.y } + 2

    println("max y on map: $maxY")

    for (x in map.first().indices) {
        map[maxY][x].type = ROCK
    }

    val entry = map[0][500]

    var sandCount = 0
    do {
        val result = entry.acceptSand()
        sandCount++
    } while (result != REFUSE)

    return sandCount - 1
}

private enum class Type(char: Char) { AIR('.'), ROCK('#'), SAND('o') }

private enum class FallResult { REFUSE, ACCEPT, ABYSS }

private class Point(var type: Type, val x: Int, val y: Int) : WithEightNeighbours<Point> {
    override var topLeft: Point? = null
    override var topRight: Point? = null
    override var bottomRight: Point? = null
    override var bottomLeft: Point? = null

    override var left: Point? = null

    override var top: Point? = null

    override var right: Point? = null

    override var bottom: Point? = null

    fun acceptSand(): FallResult {
//        println("$this accepting sand")

        if (this.type != AIR) return REFUSE

        val didFallDown = bottom?.acceptSand() ?: ABYSS
        if (didFallDown != REFUSE) return didFallDown

        val didFallLeft = bottomLeft?.acceptSand() ?: ABYSS
        if (didFallLeft != REFUSE) return didFallLeft

        val didFallRight = bottomRight?.acceptSand() ?: ABYSS
        if (didFallRight != REFUSE) return didFallRight

        this.type = SAND
        return ACCEPT
    }

    override fun toString(): String {
        return "Point of x: $x, y: $y, type: $type"
    }
}

private fun String.applyIntructionTo(map: Array<Array<Point>>) {

    val points = this.split(" -> ")
        .map { entry ->
            entry.split(",").map { pos -> pos.toInt() }.let { (first, second) -> Position(first, second) }
        }

//    println("constructing map from $points")

    points.windowed(2, step = 1).forEach { (firstPos, secondPos) ->
        val xRange = if (firstPos.x < secondPos.x) firstPos.x..secondPos.x else firstPos.x downTo secondPos.x
        val yRange = if (firstPos.y < secondPos.y) firstPos.y..secondPos.y else firstPos.y downTo secondPos.y


        for (x in xRange) {
            for (y in yRange) {
                val point = map[y][x]
                point.type = ROCK
            }
        }
    }
}

private fun makeMap(): Array<Array<Point>> {
    val emptyArray = Array(1000) { yIndex ->
        Array(1000) { xIndex ->
            Point(AIR, x = xIndex, y = yIndex)
        }
    }

    return emptyArray
}