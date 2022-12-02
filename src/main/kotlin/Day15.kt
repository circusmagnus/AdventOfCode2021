fun day15(input: List<String>): Int {

    val (map, chitons) = makeChitonMap(input)

    chitons.forEach { it.establishNeighbours(map) }

    tailrec fun takeStep(unVisitedChitons: MutableSet<ChitonPoint>): Int {
        val currentPoint = unVisitedChitons.minByOrNull { chitonPoint -> chitonPoint.riskFromStart }!!

        return if(currentPoint.isEnd(map)) currentPoint.riskFromStart
        else {
            currentPoint.visitAndCalcRiskForNeighbours()
            unVisitedChitons.remove(currentPoint)
            takeStep(unVisitedChitons)
        }
    }

    return takeStep(chitons)
}

private data class ChitonPoint(val x: Int, val y: Int, val riskLevel: Int) {
    var isVisited = false
    var riskFromStart = if (this.isStart()) 0 else Int.MAX_VALUE

    private var upper: ChitonPoint? = null
    private var right: ChitonPoint? = null
    private var lower: ChitonPoint? = null
    private var left: ChitonPoint? = null

    private fun getNeighbours() =
        listOfNotNull(upper, right, lower, left)

    fun cloneRight(map: ChitonMap) = ChitonPoint(
        x = this.x + map.first().size,
        y = this.y,
        riskLevel = if(riskLevel < 9) riskLevel + 1 else 1
    )

    fun cloneDown(map: ChitonMap) = ChitonPoint(
        x = this.x,
        y = this.y + map.size,
        riskLevel = if(riskLevel < 9) riskLevel + 1 else 1
    )


    fun establishNeighbours(map: ChitonMap) {
        upper = if (hasUpperNeighbour(map)) map[y - 1][x] else null
        right = if (hasRightNeighbour(map)) map[y][x + 1] else null
        lower = if (hasLowerNeighbour(map)) map[y + 1][x] else null
        left = if (hasLeftNeighbour(map)) map[y][x - 1] else null
    }

    fun visitAndCalcRiskForNeighbours() {
        getNeighbours().forEach { neighbour ->
            val riskThroughThis = this.riskFromStart + neighbour.riskLevel
            val riskFromStartForNeigbour =
                if (riskThroughThis < neighbour.riskFromStart) riskThroughThis else neighbour.riskFromStart
            neighbour.riskFromStart = riskFromStartForNeigbour
        }
        isVisited = true
    }

    fun isStart() = this.x == 0 && this.y == 0
    fun isEnd(map: ChitonMap) = this.x == map.last().lastIndex && this.y == map.lastIndex
}

private typealias ChitonMap = Array<Array<ChitonPoint>>
private typealias Way = List<ChitonPoint>

private fun makeChitonMap(data: List<String>): Pair<ChitonMap, MutableSet<ChitonPoint>> {
    val initialChitons = mutableSetOf<ChitonPoint>()
    val map = data.mapIndexed { y, row ->
        row.mapIndexed { x, riskLevel ->
            ChitonPoint(x, y, riskLevel.digitToInt()).also { initialChitons.add(it) }
        }.toTypedArray()
    }.toTypedArray()

//    tailrec fun cloneRight(step: Int, previousTile: Set<ChitonPoint>, allChitons: MutableSet<ChitonPoint>): MutableSet<ChitonPoint> {
//        if(step == 6) return allChitons
//        val newTile = mutableSetOf<ChitonPoint>()
//        else {
//            for (chiton in previousTile) {
//                val cloned = chiton.cloneRight(map)
//            }
//        }
//    }

    return map to initialChitons
}

private fun ChitonPoint.hasLeftNeighbour(map: ChitonMap) = x > 0
private fun ChitonPoint.hasUpperNeighbour(map: ChitonMap) = y > 0
private fun ChitonPoint.hasLowerNeighbour(map: ChitonMap) = y < map.lastIndex
private fun ChitonPoint.hasRightNeighbour(map: ChitonMap) = x < map.first().lastIndex