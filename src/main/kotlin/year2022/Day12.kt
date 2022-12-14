package year2022

import WithFourNeighbours

fun day12(data: List<String>): Int {
    val toVisit = makeAndInitMap(data)

    tailrec fun takeStep(currentPoint: GridPoint, unVisited: MutableList<GridPoint>): Int {

        return if (currentPoint.elevation == 'a'.code) currentPoint.currentDistance
        else {
            currentPoint.routeThrough()
            val newCurrent = unVisited.minByOrNull { point -> point.currentDistance }!!
            unVisited.remove(newCurrent)
            takeStep(newCurrent, unVisited)
        }
    }

    val start = toVisit.first { it.isEnd }
    start.currentDistance = 0

    return takeStep(start, toVisit.filterNot { it.isEnd }.toMutableList())
}

private fun makeAndInitMap(data: List<String>): List<GridPoint> {
    val map = makeMap(data)
    initPoint(map)
    return map.flatten()
}

private fun makeMap(data: List<String>): Array<Array<GridPoint>> {
    return data.mapIndexed { yIndex, row ->
        Array(row.length) { xIndex ->
            val char = row[xIndex]
            val isEnd = char == 'E'
            val isStart = char == 'S'
            val elevation = if (isStart) 'a'.code else if (isEnd) 'z'.code else char.code
            GridPoint(1, elevation, isEnd)
        }
    }.toTypedArray()
}

private fun initPoint(grid: Array<Array<GridPoint>>) {

    for (y in grid.indices) {
        for (x in grid.first().indices) {
            val point = grid[y][x]
            point.left = if (x > 0) grid[y][x - 1] else null
            point.top = if (y > 0) grid[y - 1][x] else null
            point.right = if (x < grid.first().lastIndex) grid[y][x + 1] else null
            point.bottom = if (y < grid.lastIndex) grid[y + 1][x] else null
        }
    }
}

private class GridPoint(val diff: Int, val elevation: Int, val isEnd: Boolean): WithFourNeighbours<GridPoint> {

    //    var visited: Boolean = false
//    var isStart: Boolean = false
    var currentDistance = Int.MAX_VALUE

    override var left: GridPoint? = null
    override var top: GridPoint? = null
    override var right: GridPoint? = null
    override var bottom: GridPoint? = null

    val neighbours get() = listOfNotNull(left, top, right, bottom)

    fun routeThrough() {
        neighbours.forEach { it.visit(currentDistance, elevation) }
//        visited = true
    }

    fun visit(diffUpToNow: Int, elevation: Int) {
        val calculatedDiff = if (elevation - this.elevation < 2) diffUpToNow + diff else Int.MAX_VALUE
        currentDistance = if (calculatedDiff < currentDistance) calculatedDiff else currentDistance
    }

    override fun toString(): String {
        return "Point with diff: $diff, elevation $elevation, isEnd: $isEnd"
    }
}