package year2021

fun day9(input: List<String>): Int {
    val map = makeMap(input)
    return map.getLargestBasinsMultiplied()
}

data class Point9(val x: Int, val y: Int, val value: Int) {
    var isCounted = false
}

private data class Basin(val size: Int, val lowPoint: Point9)

private class Map9(innerMap: Array<IntArray>) {

    private val pointsMap = innerMap
        .mapIndexed { y, line ->
            line
                .mapIndexed { x, value -> Point9(x = x, y = y, value = value) }
        }

    fun riskLevelForAll() = findLowPoints().sumOf { it.calculateRiskLevel() }

    fun getLargestBasinsMultiplied(): Int {
        val sizes = getBasins().findThreeLargest().onEach { println("big basin: $it") }.map { it.size }
        return sizes.first() * sizes[1] * sizes[2]
    }

    private fun getBasins() = findLowPoints()
        .map { lowPoint ->
            val basinSize = countWithChildren(lowPoint, null)
            Basin(basinSize, lowPoint)
        }

    private fun List<Basin>.findThreeLargest() = sortedByDescending { it.size }.take(3)

    private fun findLowPoints(): List<Point9> {
        val lowPoints = mutableListOf<Point9>()
        for (y in 0..pointsMap.lastIndex) {
            for (x in 0..pointsMap.first().lastIndex) {
                val point = pointsMap[y][x]
                if (isLow(point)) {
                    println("found low Point: $point with value: ${point.value}]}")
                    lowPoints.add(point)
                }
            }
        }
        return lowPoints
    }

    private fun Point9.calculateRiskLevel(): Int = 1 + pointsMap[y][x].value

    private fun isLow(point: Point9): Boolean {
        val (x, y, _) = point
        return (if (point.hasLeftNeighbour) pointsMap[y][x].value < pointsMap[y][x - 1].value else true) &&
                (if (point.hasUpperNeighbour) pointsMap[y][x].value < pointsMap[y - 1][x].value else true) &&
                (if (point.hasLowerNeighbour) pointsMap[y][x].value < pointsMap[y + 1][x].value else true) &&
                (if (point.hasRightNeighbour) pointsMap[y][x].value < pointsMap[y][x + 1].value else true)
    }

    private val Point9.hasLeftNeighbour get() = x > 0
    private val Point9.hasUpperNeighbour get() = y > 0
    private val Point9.hasLowerNeighbour get() = y < pointsMap.lastIndex
    private val Point9.hasRightNeighbour get() = x < pointsMap.first().lastIndex

    fun countWithChildren(point: Point9, parent: Point9?): Int {

        if (point.isCounted || point.value >= 9) return 0

        val upperLeaf: Point9? = if (point.hasUpperNeighbour) pointsMap[point.y - 1][point.x] else null

        val rightLeaf: Point9? = if (point.hasRightNeighbour) pointsMap[point.y][point.x + 1] else null

        val lowerLeaf: Point9? = if (point.hasLowerNeighbour) pointsMap[point.y + 1][point.x] else null

        val leftLeaf: Point9? = if (point.hasLeftNeighbour) pointsMap[point.y][point.x - 1] else null

        val nonNullLeafs = listOfNotNull(upperLeaf, rightLeaf, lowerLeaf, leftLeaf)

        point.isCounted = true

        val count = 1 + nonNullLeafs.sumOf { leaf -> countWithChildren(leaf, point) }

        println("$point with parent of $parent is counted: $count")


        return count
    }
}

private fun makeMap(data: List<String>): Map9 = data.map { line ->
    line.map { it.digitToInt() }.toIntArray()
}.toTypedArray()
    .let { Map9(it) }

