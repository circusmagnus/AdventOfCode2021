package year2022

import year2022.ActionStatus.*

fun day16(data: List<String>): Int {
    val valvesToConnect = data.map { it.makeValve() }
    data.makeConnections(valvesToConnect)
//    println("after connections")
    val distances = Distances(makeDistancesMatrix(valvesToConnect), valvesToConnect)
    valvesToConnect.forEach { valve -> valve.distances = distances }
//    println("after distances")

    val valvesSorted = valvesToConnect.sortedByDescending { it.flowRate }

    val valveA = valvesSorted.first { it.name == "AA" }

    return makeTurns(valvesSorted, distances)

//    valveA.getBestValue(null, 31, valvesSorted, 0).second
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

//    fun santaWithElephant(timeLeft: Int, valves: List<Valve>): Int {
//        val (santaResult, _) = getBestValue(null, timeLeft, valves, 0)
//        val santaStart = santaResult[santaResult.lastIndex - 1].valve
//        val (eleResult, _) = getBestValue(null, timeLeft, valves - santaStart, 0)
//        val eleStart = eleResult[eleResult.lastIndex - 1].valve
//
////        println("santa path: $santaResult")
////        println("ele path: $eleResult")
////
////        println("santa start at $santaStart, ele start at $eleStart")
//
//        tailrec fun negotiate(
//            santaStart: Valve,
//            santasPossibilities: List<Valve>,
//            elephantStart: Valve,
//            elephantPossibilities: List<Valve>,
//            cumulativeResult: Int
//        ): Int {
////            println("negotiating with santa start: $santaStart and poss: $santasPossibilities")
////            println("negotiating with ele start: $elephantStart and poss: $elephantPossibilities")
//
//            if (santasPossibilities.intersect(elephantPossibilities.toSet()).isEmpty()) return cumulativeResult
//
//            val (santasPath, santasValue) = santaStart.getBestValue(this, timeLeft, santasPossibilities, 0)
//
//            val (elephantPath, elephantValue) = elephantStart.getBestValue(this, timeLeft, elephantPossibilities, 0)
//
//            val sureSantaValves = santasPath.takeWhile { santaValve ->
//                val sameElephantValve = elephantPath.firstOrNull { it.valve == santaValve.valve }
//                sameElephantValve?.let { it.pressureReleased < santaValve.pressureReleased }
//                    ?: true
//            }.map { it.valve }
//
//            val sureElephantValves = elephantPath.takeWhile { elephantValve ->
//                val sameSantaValve = santasPath.firstOrNull { it.valve == elephantValve.valve }
//                sameSantaValve?.let { it.pressureReleased <= elephantValve.pressureReleased }
//                    ?: true
//            }.map { it.valve }
//
//            println("sure santa valves are: $sureSantaValves")
//            println()
//            println("sure ele valves are: $sureElephantValves")
//
//
//            val newSantaPoss = valves - sureElephantValves.toSet()
//            val newElePoss = valves - sureSantaValves.toSet()
//
//            return negotiate(santaStart, newSantaPoss, elephantStart, newElePoss, santasValue + elephantValue)
//        }
//
//        return negotiate(santaStart, valves - eleStart, eleStart, valves - santaStart, 0)
//
//    }

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
