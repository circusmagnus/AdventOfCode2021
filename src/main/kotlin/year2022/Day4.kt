package year2022

fun day4(data: List<String>): Int {
    val count = data.map { elfPair -> elfPair.splitIntoPair() }
        .count { (firstRange, secondRange) -> firstRange.overlap(secondRange) }

    return count
}

private fun IntRange.fullyContains(other: IntRange): Boolean{
    return (this.first <= other.first && this.last >= other.last) ||
            (other.first <= this.first && other.last >= this.last)
}

private fun IntRange.overlap(other: IntRange): Boolean{
    return (this.first <= other.first && this.last >= other.first) ||
            (other.first <= this.first && other.last >= this.first)
}

private fun String.splitIntoPair(): Pair<IntRange, IntRange> {
    return split(",")
        .map { stringRange ->
            stringRange.split("-")
                .map { it.toInt() }
                .let { (start, end) -> start..end }
        }.let { (firstElf, secondElf) -> Pair(firstElf, secondElf) }
}

private class Assginement
