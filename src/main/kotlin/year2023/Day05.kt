package year2023

import splitBy
import kotlin.math.max
import kotlin.math.min

/**
 * seeds damy na obiekty w liście
 *
 * pozostałe przekształcimy w mapę range id typu > range id drugiego typu
 *
 * w zasadzie chyba musimy zrobic mapowanie, a potem wziac seed, isc z nim przez mape i na koniec wypluc
 *
 * wystarcza nam switche - o ile trzeba przesunac id w drugim typie (ewnetualnie o zero)
 */

fun day5(input: List<String>): Long {
    val parts = input.splitBy { it.isBlank() }

//    println("parts: $parts")

    val switchGroups = parts
        .drop(1)
        .map { section ->
//            println("section: $section")
            section.toSwitchEntries().map { it.toSwith() }
        }.map { switches ->
//            println("got switches: $switches")
            SwitchGroup(switches)
        }

//    println("switchGroups: $switchGroups")

    val locations = parts.seedpart()
        .first()
        .toSeedRanges()
        .sortedBy { it.value.first }
//        .toRange()
        .map { seedRange: SeedRange ->
            goThroughSwitchGroups(remaining = switchGroups, listOf(seedRange.value))
        }.flatten()


    return locations.minOf { it.first }
}

data class SeedRange(val value: LongRange)

private tailrec fun goThroughSwitchGroups(remaining: List<SwitchGroup>, incomingRanges: List<LongRange>): List<LongRange> {
//    println("going through swith groups. incomingId: $incomingId")
    val sortedRanges = incomingRanges.sortedBy { it.first }
    if (remaining.isEmpty()) return sortedRanges

    val head = remaining.first()
    val newRanges = head.getNewRanges(sortedRanges)

//    println("newId after switch $head is $newId")

    return goThroughSwitchGroups(remaining.drop(1), newRanges)
}

private fun String.toSeedRanges(): List<SeedRange> {
    return split("seeds: ")
        .last()
        .split(" ")
        .map { it.toLong() }
        .chunked(2)
        .map { (first, second) -> SeedRange(first until (first + second)) }
}

class SwitchGroup(switches: List<Switch>) {

    private val switches = switches.sortedBy { it.sourceRange.first }

    fun getNewRanges(ranges: List<LongRange>): List<LongRange> {

        tailrec fun goThroughSwitches(
            remaining: List<Switch>,
            mappedRanges: List<LongRange>,
            unMappedRanges: List<LongRange>
        ) : List<LongRange> {
            if (remaining.isEmpty()) return mappedRanges + unMappedRanges

            val switch = remaining.first()
            val (newMapped, newUnmapped) = goThroughOneSwitch(switch, mappedRanges, unMappedRanges)

            return goThroughSwitches(remaining.drop(1), newMapped, newUnmapped.sortedBy { it.first })
        }

        return goThroughSwitches(remaining = switches, mappedRanges = emptyList(), unMappedRanges = ranges)
    }

    private tailrec fun goThroughOneSwitch(
        switch: Switch,
        mappedRanges: List<LongRange>,
        unMappedRanges: List<LongRange>
    ): Pair<List<LongRange>, List<LongRange>> {
        if (unMappedRanges.isEmpty()) return Pair(mappedRanges, unMappedRanges)

        val query = unMappedRanges.first()
        val (smaller, fitting, greater) = switch.getRanges(query)
        if (smaller == null && fitting == null) return Pair(mappedRanges, unMappedRanges)

        val newMapped = (mappedRanges + smaller + fitting).filterNotNull()
        val newUnmapped = (unMappedRanges.drop(1) + greater).filterNotNull().sortedBy { it.first }

        return goThroughOneSwitch(switch, newMapped, newUnmapped)
    }
}

private fun List<List<String>>.toSwitches(select: List<List<String>>.() -> List<String>): List<Switch> {
    return select().toSwitchEntries().map { it.toSwith() }
}

private fun List<String>.toSwitchEntries(): List<SwitchEntry> {
    val rawEntries = splitBy { it.contains("map") }
//    println("rawEntries: $rawEntries")
    val last = rawEntries.last()

    return rawEntries.map { entry ->
        entry.map { switchEntry -> switchEntry.toSwitchEntry() }
    }.flatten()
}

private fun String.toSwitchEntry(): SwitchEntry {
    val splitted = split(" ")
    val destStart = splitted.first().toLong()
    val sourceStart = splitted[1].toLong()
    val rangeSize = splitted.last().toLong()
    return SwitchEntry(sourceStart = sourceStart, destStart = destStart, rangeSize = rangeSize)
}

data class SwitchEntry(val sourceStart: Long, val destStart: Long, val rangeSize: Long)

fun SwitchEntry.toSwith() = Switch(
    sourceRange = sourceStart until (sourceStart + rangeSize),
    switchBy = destStart - sourceStart
)

data class Switch(val sourceRange: LongRange, val switchBy: Long) {

    fun getRanges(range: LongRange): Triple<LongRange?, LongRange?, LongRange?> {
        val smaller = if (range.first < sourceRange.first) range.first .. min(range.last, (sourceRange.first - 1)) else null
        val greater = if (range.last > sourceRange.last) max(range.first, (sourceRange.last + 1))..range.last else null
        val fitting = if (range.first <= sourceRange.last && range.last >= sourceRange.first) {
            max(range.first, sourceRange.first)..min(range.last, sourceRange.last)
        } else {
            null
        }
        return Triple(smaller, fitting?.let { switch(it) }, greater)
    }

    private fun switch(range: LongRange) = range.first + switchBy..range.last + switchBy

//    private fun split(range)

    fun switch(id: Long): Long {
        val doesMatch = id in sourceRange
//        println("$id does match in $this: $doesMatch")
        val newId = if (doesMatch) id + switchBy else id
//        println("switched id: $id by $switchBy to $newId")
        return newId
    }
}

private fun List<List<String>>.seedpart() = first()
//    .also { println("seedPart is: $it") }

