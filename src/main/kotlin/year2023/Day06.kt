package year2023

/**
 * Znajdz pierwszy wynik, ktory przechodzi
 * Znajdz analogiczny od tylu
 * Policz ile jest pomiedzy
 */
fun day6(input: List<String>): Long {

    val times = input.first().getData()
    println("times: $times")
    val records = input.last().getData()
    println("records: $records")

    val timeRecords = getTimeRecordPairs(listOf(times), listOf(records), emptyList())
    println("time-records: $timeRecords")

    return timeRecords
        .map { it.findWins() }
        .onEach { println("win in $it times") }
        .reduce { acc, i -> acc * i }
}
private fun Pair<Long, Long>.findWins(): Long {
    val (time, record) = this
    val originalRoundedDownHoldTime = countHoldTime(time, record)
    val minimumHoldTime = originalRoundedDownHoldTime + 1
//    val minimumSpeedingTime = minimumSpeed * time
//    val minHoldingTime
    val maxHoldTime = time - minimumHoldTime

//    println("finding wins. origin speed: $originalRoundedDownHoldTime, minimum speed: $minimumHoldTime, max speed: $maxHoldTime")

    return (maxHoldTime - minimumHoldTime) + 1
}
private fun countHoldTime(time: Long, distance: Long, roundDown: Boolean = true): Long {

    tailrec fun run(holdingTime: Long, prevTraveled: Long): Long {
//        println("holdingTime: $holdingTime, prevTraveled: $prevTraveled")

        if(prevTraveled > distance) return (holdingTime - 2)

        val travelTime = time - holdingTime
        val actuallyTraveled = travelTime * holdingTime

        return run(holdingTime + 1, prevTraveled = actuallyTraveled)
    }

    return run(holdingTime = 0, prevTraveled = 0)
}

private tailrec fun getTimeRecordPairs(times: List<Long>, records: List<Long>, acc: List<Pair<Long, Long>>): List<Pair<Long, Long>> {
    if (times.isEmpty()) return acc

    val newPair = times.first() to records.first()
    val newAcc = acc + newPair

    return getTimeRecordPairs(times.drop(1), records.drop(1), newAcc)
}

private fun String.getData() = split(":")
    .last()
    .split(" ")
    .filter { it.isNotBlank() }
    .joinToString(separator = "") { it.trim() }
    .toLong()