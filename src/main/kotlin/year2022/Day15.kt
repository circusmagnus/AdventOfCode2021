package year2022

import Position
import WithPosition
import kotlin.math.abs

fun day15(data: List<String>): Long {
    val sensorAndBeacons = getSensorAndBeaconPairs(data)
//    println("sensor and beacon pairs are: $sensorAndBeacons")
    val sensors = sensorAndBeacons.map { (sensor, beacon) ->
        val dist = (sensor.position to beacon.position).countDist()
        sensor.radius = dist.total
        sensor
    }.sortedByDescending { it.radius }
//    println("distances are: $distances")

    val max = 4000000

    fun findDistress(): Position {

        tailrec fun scanX(currentX: Int, currentY: Int, sensorsLeft: List<Sensor>): Position? {
            if (currentX > max) return null

            val newPos = Position(currentX, currentY)

            val (sensor, distanceToSensor) = sensorsLeft.firstNotNullOfOrNull { sensor ->
                val distance = (newPos to sensor.position).countDist()
                if (distance.total <= sensor.radius) sensor to distance else null
            } ?: return newPos

            val nextX = currentX + distanceToSensor.x + (sensor.radius - abs(distanceToSensor.y)) + 1
//                if (distanceToSensor.x > 0) currentX + distanceToSensor.x + (sensor.radius - abs(distanceToSensor.y))
//                else if (distanceToSensor.x == 0) currentX + (sensor.radius - abs(distanceToSensor.y))
//                else currentX + (sensor.radius - abs(distanceToSensor.y)) - distanceToSensor.x

            return scanX(nextX, currentY, sensorsLeft - sensor)
        }

        for (y in 0..max) {
            val found = scanX(0, y, sensors)
            if (found != null) return found
        }

        throw IllegalStateException()
    }

    val distress = findDistress()
    println("distress: $distress")
    val freq = distress.x.toLong() * 4000000L + distress.y
    return freq
}

//private fun countInRange(y: Int, origin: WithPosition, dist: Int): Set<Position> {
//    val distY = abs(origin.position.y - y)
//    val maxDistX = (dist - distY).takeUnless { it < 0 } ?: return emptyList()
//    val xRange = origin.position.x - maxDistX..origin.position.x + maxDistX
//    return buildSet {
//        for (x in xRange) {
//            add(Position(x = x, y = y))
//        }
//    }
////    val rangeX = origin.position.x - maxDistX .. origin.position.x + maxDistX
////    tailrec fun go
//}

private fun getSensorAndBeaconPairs(data: List<String>): List<Pair<Sensor, Beacon>> = data.map { entry ->
    entry.getSensorAndBeacon()
}

private data class Distance(val x: Int, val y: Int) {
    val total: Int get() = abs(x) + abs(y)
}

private fun Pair<Position, Position>.countDist(): Distance {
    val distX = second.x - first.x
    val distY = second.y - first.y
    return Distance(distX, distY)
}

private fun String.getSensorAndBeacon(): Pair<Sensor, Beacon> {
    val entry = this.split("Sensor at ").last().split(": closest beacon is at ")
    val sensorPosEntry = entry.first()
    val sensor = sensorPosEntry.split(",").let { (xPos, yPos) ->
        val x = xPos.filter { it.isDigit() }.toInt().let { abs -> if (xPos.contains('-')) -abs else abs }
        val y = yPos.filter { it.isDigit() }.toInt().let { abs -> if (yPos.contains('-')) -abs else abs }
        val pos = Position(x, y)
        Sensor(pos)
    }
    val beaconEntry = entry.last()

    val beacon = beaconEntry.split(",").let { (xPos, yPos) ->
        val x = xPos.filter { it.isDigit() }.toInt().let { abs -> if (xPos.contains('-')) -abs else abs }
        val y = yPos.filter { it.isDigit() }.toInt().let { abs -> if (yPos.contains('-')) -abs else abs }
        val pos = Position(x, y)
        Beacon(pos)
    }

    return Pair(sensor, beacon)
}

private data class Sensor(override val position: Position) : WithPosition {
    var radius: Int = 0
}

private data class Beacon(override val position: Position) : WithPosition