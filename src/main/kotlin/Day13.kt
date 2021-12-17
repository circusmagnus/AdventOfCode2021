fun day13(input: List<String>): Int {

    val (paper, dots) = drawPaperAndDots(input)
    val foldsData = findFoldLines(input)

    tailrec fun foldAll(folds: List<Fold>, dots: Collection<Dot>): Collection<Dot> {
        return if (folds.isEmpty()) dots
        else {
            val folded = fold(folds.first(), dots)
            foldAll(folds.drop(1), folded)
        }
    }

    val foldedDots = foldAll(foldsData, dots)

    val newPaper = paintMapWithDots(foldedDots.toList())

    for(y in newPaper.indices) {
        for(x in newPaper.first().indices) {
            print(newPaper[y][x])
        }
        println("")
    }

    return foldedDots.size
}

@OptIn(ExperimentalStdlibApi::class)
private fun fold(foldData: Fold, dots: Collection<Dot>): Set<Dot> {
    return buildSet<Dot> {
        if (foldData.xAxis) foldLeft(foldData.position, dots) else foldUp(foldData.position, dots)
    }
}

private fun MutableSet<Dot>.foldUp(yAxis: Int, dots: Collection<Dot>) {
    dots.filter { it.y < yAxis }.forEach { add(it) }
    dots
        .filter { it.y > yAxis }
        .map { dot ->
            val linesFromFold = dot.y - yAxis
            val newY = yAxis - linesFromFold
            dot.copy(y = newY)
        }.forEach { add(it) }


}

private fun MutableSet<Dot>.foldLeft(xAxis: Int, dots: Collection<Dot>) {
    dots.filter { it.x < xAxis }.forEach { add(it) }
    dots
        .filter { it.x > xAxis }
        .map { dot ->
            val linesFromFold = dot.x - xAxis
            val newX = xAxis - linesFromFold
            dot.copy(x = newX)
        }.forEach { add(it) }

}

private fun drawPaperAndDots(input: List<String>): Pair<Array<CharArray>, List<Dot>> {
    val justDots = input
        .filter { it.isNotEmpty() && it.contains("fold").not() }
        .map { line ->
            val splitted = line.split(",")
            val (x, y) = splitted
            Dot(x.toInt(), y.toInt())
        }

    return Pair(paintMapWithDots(justDots), justDots)
}

private fun paintMapWithDots(justDots: List<Dot>): Array<CharArray> {
    val maxX = justDots.maxOf { it.x }
    val maxY = justDots.maxOf { it.y }

    val paper = Array(maxY + 1) { y ->
        CharArray(maxX + 1) { x ->
            val maybeDot = justDots.firstOrNull { it.x == x && it.y == y }
            maybeDot?.let { '#' } ?: '.'
        }
    }

    return paper
}

data class Dot(val x: Int, val y: Int)

data class Fold(val xAxis: Boolean, val position: Int)

private fun findFoldLines(input: List<String>): List<Fold> {
    return input
        .filter { it.startsWith("fold") }
        .map { line ->
            line
                .split(" ")
                .last()
                .split("=")
                .let { (axis, position) -> Fold(axis == "x", position.toInt()) }
        }
}