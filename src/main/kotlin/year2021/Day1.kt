package year2021

import tail

@ExperimentalStdlibApi
fun countIncreases(data: List<String>) : Int {
    val depths = data.map { it.toInt() }

    tailrec fun iterate(toRead: List<Int>, previous: Int, increasesCount: Int) : Int {
        return if(toRead.isEmpty()) increasesCount
        else {
            val head = toRead.first()
            val newIncreases = if(head > previous) increasesCount + 1 else increasesCount
            iterate(toRead.tail(), head, newIncreases)
        }
    }

    val windows = depths.windowed(3, 1).map { it.sum() }


    return iterate(windows, Int.MAX_VALUE, 0)
}

@ExperimentalStdlibApi
private fun<T> List<T>.windowed(size: Int, step: Int): List<List<T>> {

    tailrec fun iterate(toRead: List<T>, windowed: List<List<T>>) : List<List<T>> {
        return if(toRead.size < size) windowed
        else {
            val window = toRead.take(size)
            val newToRead = toRead.drop(step)
            val newWindowed = buildList {
                windowed.forEach { add(it) }
                add(window)
            }
            iterate(newToRead, newWindowed)
        }
    }

    return iterate(this, emptyList())
}