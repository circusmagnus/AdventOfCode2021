package year2023

fun day9(input: List<String>): Long {
    val inputSeq = input.toSequences()

    val results = inputSeq.map { findLastValue(it) }.also { println("results: $it") }

    return results.sum()
}

private fun String.toSequence(): List<Long> {
    return split(" ")
        .map { it.toLong() }
}

private fun List<String>.toSequences(): List<List<Long>> {
    return map { it.toSequence() }
}

private fun findLastValue(input: List<Long>): Long {

    tailrec fun getNextSequence(prev: List<Long>, next: List<Long>): List<Long> {
        if (prev.size < 2) return next
//            .also { println("next sequence found: $it from seq of: $input") }

        val a = prev.first()
        val b = prev[1]
        val diff = b - a
        val nextSeq = next + diff
        return getNextSequence(prev.drop(1), nextSeq)
    }

    tailrec fun getSequences(current: List<Long>, acc: List<List<Long>>): List<List<Long>> {
        if (current.all { it == 0L }) return acc

        val next = getNextSequence(current, emptyList())
        return getSequences(next, buildList { addAll(acc); add(current) })
    }

    tailrec fun findLast(allSeq: List<List<Long>>, currLastValue: Long): Long {
        if (allSeq.isEmpty()) return currLastValue

        val newLast = (allSeq.last().last() + currLastValue)
        return findLast(allSeq.dropLast(1), newLast)

//        if (allSeq.size == 1) return currLastValue
////            .also { println("last value found: $it from $input") }
//
//        val newLast = (allSeq.last().last() + currLastValue)
//        return findLast(allSeq.dropLast(1), newLast)
    }

    tailrec fun findFirst(allSeq: List<List<Long>>, currFirstValue: Long): Long {
        if (allSeq.isEmpty()) return currFirstValue

        val newFirst = (allSeq.last().first() - currFirstValue)
        return findFirst(allSeq.dropLast(1), newFirst)
    }

    val sequences = getSequences(input, emptyList())
    return findFirst(sequences, 0)
}