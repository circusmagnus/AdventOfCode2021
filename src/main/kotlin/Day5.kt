fun day5(input: List<String>): Int {
    val lines = input.map { lineData -> Line.fromEntry(lineData) }
    val withoutDiagonals = lines.filter { it.isNotDiagonal }

    val map = Map(lines)
    map.drawLines(lines)

    return map.dangerous

}

private class Map(val lines: List<Line>) {

    val map = drawEmpty()

    val dangerous: Int
    get() = map.sumOf { column -> column.count { value -> value >= 2 } }

    private fun drawEmpty(): Array<IntArray> {
        val maxX = lines.map { listOf(it.firstPoint.x, it.lastPoint.x) }.flatten().maxOf { it }
        val maxY = lines.map { listOf(it.firstPoint.y, it.lastPoint.y) }.flatten().maxOf { it }

        println("maxX = $maxX, maxy = $maxY")

        return Array(maxY + 1) { index ->
            IntArray(maxX + 1) { 0 }
        }
    }

    fun drawLines(lines: List<Line>) {
        lines.forEach { line ->
            line.allPoints.forEach { point -> point.drawOn(map) }
        }

//        println("lines drawn:")
//
//        for (i in 0..map.lastIndex) {
//            println("")
//            for (j in 0..map[i].lastIndex) {
//                val value = map[i][j]
//                print(value)
//            }
//        }
    }

}


private data class Point(val x: Int, val y: Int) {
    fun drawOn(map: Array<IntArray>) {
        map[y][x] = map[y][x] + 1
    }
}

private class Line(val firstPoint: Point, val lastPoint: Point) {

    val isNotDiagonal
        get() = isVertical || isHorizontal

    val isVertical
        get() = firstPoint.x == lastPoint.x
    val isHorizontal
        get() = firstPoint.y == lastPoint.y

    val allPoints: List<Point>
        get() = when {
            isVertical -> getYkses().map { y -> Point(x = firstPoint.x, y = y) }
            isHorizontal -> getXses().map { x -> Point(x = x, y = firstPoint.y) }
            else -> getDiagonals()
        }



    private fun getXses() =
        if (areXsesIncremental()) firstPoint.x..lastPoint.x
        else firstPoint.x downTo lastPoint.x

    private fun areXsesIncremental() = firstPoint.x <= lastPoint.x
    private fun areYsesIncremental() = firstPoint.y <= lastPoint.y


    private fun getYkses() =
        if (areYsesIncremental()) firstPoint.y..lastPoint.y
        else firstPoint.y downTo lastPoint.y

    @OptIn(ExperimentalStdlibApi::class)
    private fun getDiagonals(): List<Point>{
        val xSes = getXses().toList()
        val ykses = getYkses().toList()
        check(xSes.size == ykses.size)

        return buildList<Point> {
            for (index in 0..xSes.lastIndex){
                add(Point(x = xSes[index], y = ykses[index]))
            }
        }
    }

        override fun toString(): String {
        return "Line: first: $firstPoint, last: $lastPoint, isNotDiagonal: ${isNotDiagonal}, allPoints: $allPoints"
    }

    companion object {
        fun fromEntry(entry: String): Line = entry
            .split("->")
            .map { pointCoordinates ->
                pointCoordinates
                    .split(",")
                    .map { it.trim().toInt() }
                    .let { (x, y) -> Point(x, y) }
            }.let { (start, end) -> Line(start, end) }
    }
}