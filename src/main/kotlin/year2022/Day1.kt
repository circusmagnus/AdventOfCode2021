package year2022

fun day1(data: List<String>): Int {

    println(data)
    return getListOfElfCalories(data)
        .sortedDescending()
        .take(3)
        .sum()
}

private fun getListOfElfCalories(data: List<String>): List<Int> {

    tailrec fun go(remaining: List<String>, counted: List<Int>): List<Int> {
        if (remaining.isEmpty()) return counted
        val entry = remaining.first()

        val newlyCounted = if (entry.isEmpty()) {
            counted + 0
        } else {
            val newElfCalories = entry.toInt()
            if (counted.isEmpty()) listOf(newElfCalories)
            else {
                val oldCountForThisElf = counted.last()
                counted.dropLast(1) + (oldCountForThisElf + newElfCalories)
            }
        }

        return go(remaining.drop(1), newlyCounted)
    }

    return go(data, emptyList())
}