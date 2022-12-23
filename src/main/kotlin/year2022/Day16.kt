package year2022

import year2022.ActionStatus.*

// 2020 is too low
// 2185 to high
// 2515 too high
// 2058 is bad (?)
fun day16(data: List<String>): Int {
    val valvesToConnect = data.map { it.makeValve() }
    data.makeConnections(valvesToConnect)
//    println("after connections")
    val distances = Distances(makeDistancesMatrix(valvesToConnect), valvesToConnect)
    valvesToConnect.forEach { valve -> valve.distances = distances }
//    println("after distances")
    val start = valvesToConnect.first { it.name == "AA" }

    val (forSanta, forEle) = valvesToConnect.split(start, distances)

    val (santaPath, santaResult) = start.getBestValue(null, 27, forSanta, 0)
    val (elePath, eleResult) = start.getBestValue(null, 27, forEle, 0)

    println("santa path: $santaPath")
    println()
    println("ele path: $elePath")


    return santaResult + eleResult

//    valveA.getBestValue(null, 31, valvesSorted, 0).second
}

private data class Rim(val valve: Valve, val fromSanta: Int, val fromEle: Int)

private fun Valve.getValueForCenterAt(other: Valve, distances: Distances): Int {
    val dist = with(distances) { distanceTo(other) }
    return -dist
}
private fun List<Valve>.split(start: Valve, distances: Distances): Pair<List<Valve>, List<Valve>> {
    val santas = mutableListOf<Valve>()
    val eles = mutableListOf<Valve>()

    tailrec fun getStartingValves(toSort: List<Valve>, target: Valve, iteration: Int = 1) {
        if (iteration > 2) return
        val newSorted = toSort.sortedByDescending { valve ->
            val distToNext = with(distances) { target.distanceTo(valve) }
            valve.flowRate * distToNext
        }
        val next = newSorted.first()
        if (iteration == 1) santas.add(next) else eles.add(next)
        getStartingValves(newSorted.drop(1), next, iteration + 1)
    }

    getStartingValves(this, start)

    val rest = this - (santas + eles).toSet()

    rest.forEach { valve ->
        val forSanta = valve.getValueForCenterAt(santas.first(), distances)
        val forEle = valve.getValueForCenterAt(eles.first(), distances)
        if(forSanta > forEle) santas.add(valve) else eles.add(valve)
    }

    return santas to eles
}

private fun makeTurns(valves: List<Valve>, distances: Distances): Int {
    val start = valves.first { it.name == "AA" }
    val timeLeft = 26

    val santa = Agent("Santa", start, timeLeft, distances)
    val ele = Agent("Ele", start, timeLeft, distances)

    tailrec fun go (toOpen: List<Valve>, runningTimeLeft: Int): Int {
        return if (runningTimeLeft < 1)  santa.pressureReleased + ele.pressureReleased
        else {
            val santaVisiting = santa.takeTurn(toOpen)
            val eleVisiting = ele.takeTurn(toOpen - santaVisiting)
            go(toOpen - (santaVisiting + eleVisiting), runningTimeLeft - 1)
        }
    }

    return go(valves - start, timeLeft)
}

private fun makeDistancesMatrix(valves: List<Valve>): Array<IntArray> {
    return Array(valves.size) { y ->
        IntArray(valves.size) { x ->
            valves[y].routeTo(valves[x], valves)
        }
    }
}

private class Distances(val matrix: Array<IntArray>, val allValves: List<Valve>) {

    fun Valve.distanceTo(other: Valve): Int {
        val yIndex = allValves.indexOf(this)
        val xIndex = allValves.indexOf(other)
        return matrix[yIndex][xIndex]
    }
}

private data class Node(val valve: Valve) {
    var tunnels = mutableListOf<Node>()
    var stepCount = Int.MAX_VALUE / 2
}

private fun Valve.routeTo(other: Valve, unVisited: List<Valve>): Int {
    val nodes = unVisited.map { Node(it) }
    nodes.forEach { node ->
        val valveTunnels = node.valve.tunnels
        val nodeTunnels = valveTunnels.map { valve ->
            nodes.first { it.valve == valve }
        }
        node.tunnels.addAll(nodeTunnels)
    }
    val start = nodes.first { it.valve == this }.also { it.stepCount = 0 }

    tailrec fun go(current: Node, unVisited: MutableList<Node>): Int {
//        println("$current looking into ${unVisited.first()}")
        return if (current.valve == other) current.stepCount
        else {
            current.tunnels.forEach {
                val stepsThroughHere = current.stepCount + 1
                it.stepCount = if (stepsThroughHere < it.stepCount) stepsThroughHere else it.stepCount
            }
            val newCurrent = unVisited.minBy { node -> node.stepCount }
            unVisited.remove(newCurrent)
            go(newCurrent, unVisited)
        }
    }

    return go(start, nodes.toMutableList().also { it.remove(start) })
}

private data class Valve(val name: String, val flowRate: Int) {

    val tunnels = mutableListOf<Valve>()
    lateinit var distances: Distances

    fun getBestValue(
        position: Valve?,
        timeLeft: Int,
        toOpen: List<Valve>,
        cumulativeRate: Int,
    ): Pair<List<Valve>, Int> {

        val routeLength = with(distances) { position?.distanceTo(this@Valve) ?: 0 }
        val timeLeftWhenArrived = timeLeft - routeLength

        if ((flowRate == 0 && position != null) || timeLeftWhenArrived < 1) {
            return listOf(this) to 0
        }

        val timeWhenThisOpened = timeLeftWhenArrived - 1

        val thisValue = flowRate * timeWhenThisOpened

        val newToOpen = toOpen - this
        if (newToOpen.isEmpty()) return listOf(this) to thisValue

        val (bestPath, bestNextValue) = newToOpen.map { valve ->
            valve.getBestValue(
                this,
                timeWhenThisOpened,
                newToOpen,
                cumulativeRate + flowRate,
            )
        }.maxBy { (bestPath, pressureReleased) -> pressureReleased }


        return buildList<Valve> { add(this@Valve); addAll(bestPath)  } to (thisValue + bestNextValue)
    }

    override fun toString(): String {
        return "Valve($name, flowRate=$flowRate)"
    }
}

private data class OpenedValve(val valve: Valve, val pressureReleased: Int)

private fun String.makeValve(): Valve {
    val name = split("Valve ").last().split(" has ").first()
    val flowrate = filter { it.isDigit() }.toInt()
    return Valve(name, flowrate)
}

private fun List<String>.makeConnections(valves: List<Valve>) {
    for (i in 0..valves.lastIndex) {
        val valve = valves[i]
        val string = this[i]
        val connectionsList = string.split("to valve").last()
        val connections = connectionsList.filter { it.isUpperCase() }.chunked(2)
            .map { leadsTo ->
                valves.first { it.name == leadsTo }
            }
        valve.tunnels.addAll(connections)
    }
}

private class Agent(val name: String, var currentPos: Valve, var timeLeft: Int, val distances: Distances) {

    var pressureReleased = 0
        private set
    var flowRate = 0
        private set

    val visiting = mutableSetOf(currentPos)
    var status: ActionStatus = Starting
        private set


    fun takeTurn(toOpen: List<Valve>): Set<Valve> {
        println("${this.name} in ${this.currentPos} taking turn with initial status: $status")
        status = when (val currentStatus = status) {
            Starting -> {
                val (path, _) = currentPos.getBestValue(null, timeLeft, toOpen, flowRate)
                if (path.size > 1) {
                    val next = path[1]
                    val dist = with(distances) { currentPos.distanceTo(next) }
                    visiting += next
                    Moving(dist - 1, next)
                } else Staying
            }

            is Moving -> {
                if (currentStatus.timeToTarget == 0) {
                    currentPos = currentStatus.target
                    Opening
                } else Moving(currentStatus.timeToTarget - 1, currentStatus.target)
            }

            Opening -> {
                flowRate += currentPos.flowRate
                val (path, _) = currentPos.getBestValue(null, timeLeft, toOpen, flowRate)
                if (path.size > 1) {
                    val next = path[1]
                    val dist = with(distances) { currentPos.distanceTo(next) }
                    visiting += next
                    Moving(dist - 1, next)
                } else Staying
            }

            Staying -> Staying
        }
        timeLeft--
        pressureReleased += flowRate

        return visiting
    }
}

private sealed interface ActionStatus {
    object Starting : ActionStatus
    data class Moving(val timeToTarget: Int, val target: Valve) : ActionStatus
    object Opening : ActionStatus
    object Staying : ActionStatus
}
