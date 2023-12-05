package year2023

import HasId
import splitBy

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
        .toSeedIds()
        .map { seedRange ->
            seedRange.value.map { id ->
                goThroughSwitchGroups(switchGroups, id)
            }
        }.flatten()

    return locations.min()
}

data class SeedRange(val value: LongRange)

private tailrec fun goThroughSwitchGroups(remaining: List<SwitchGroup>, incomingId: Long): Long {
//    println("going through swith groups. incomingId: $incomingId")

    if(remaining.isEmpty()) return incomingId

    val head = remaining.first()
    val newId = head.getNewId(incomingId)

//    println("newId after switch $head is $newId")

    return goThroughSwitchGroups(remaining.drop(1), newId)
}

private fun String.toSeedIds(): List<SeedRange> {
    return split("seeds: ")
        .last()
        .split(" ")
        .map { it.toLong() }
        .chunked(2)
        .map { (first, second) -> SeedRange(first until (first + second)) }
}

data class SwitchGroup(val switches: List<Switch>) {
    fun getNewId(currentId: Long): Long {

        tailrec fun run(remaining: List<Switch>, destinationId: Long): Long {
            if(destinationId != currentId || remaining.isEmpty()) return destinationId

            val head = remaining.first()
            val newId = head.switch(destinationId)

            return run(remaining.drop(1), newId)
        }

        return run(remaining = switches, destinationId = currentId)
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
    sourceRange = sourceStart until(sourceStart + rangeSize),
    switchBy = destStart - sourceStart
)

data class Switch(val sourceRange: LongRange, val switchBy: Long) {

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

