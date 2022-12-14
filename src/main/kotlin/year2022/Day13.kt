package year2022

import java.lang.StringBuilder

fun day13(data: List<String>): Int {

    val divider1 = Elem.EList(
        mutableListOf(
            Elem.EList(
                mutableListOf(Elem.EList(mutableListOf(Elem.EInt(2))))
            )
        )
    )

    val divider2 = Elem.EList(
        mutableListOf(
            Elem.EList(
                mutableListOf(Elem.EList(mutableListOf(Elem.EInt(6))))
            )
        )
    )
    val pairs = getPairs(data).map { it.toElems() }.onEach { println("pair: $it") }

    val allPackets = pairs.map { (first, second) -> listOf(first, second) }.flatten()
    val withDividers = buildList {
        addAll(allPackets)
        add(divider1)
        add(divider2)
    }

    val sorted = withDividers.sorted()

    val indices = sorted.mapIndexedNotNull { index, packet ->
        if (packet == divider1 || packet == divider2) index + 1 else null
    }


    return indices.let { (first, second) -> first * second }
}


private fun getPairs(data: List<String>): List<Pair<String, String>> {
    return data.chunked(3) { entry ->
        entry.filterNot { it.isEmpty() }.let { (first, second) -> Pair(first, second) }
    }
}

private fun Pair<String, String>.toElems(): Pair<Elem.EList, Elem.EList> {
    val (firstEntry, secondEntry) = this

    val first = parseLine(firstEntry, Elem.EList(mutableListOf(), null), StringBuilder())
    val second = parseLine(secondEntry, Elem.EList(mutableListOf(), null), StringBuilder())

    return first to second
}

private sealed interface Elem {
    val parent: EList?

    data class EInt(val value: Int, override val parent: EList? = null) : Elem, Comparable<EInt> {
        override fun compareTo(other: EInt): Int {
            return this.value.compareTo(other.value)
        }


        override fun toString(): String {
            return "$value"
        }
    }

    data class EList(val list: MutableList<Elem>, override val parent: EList? = null) : Elem, MutableList<Elem> by list,
        Comparable<EList> {

        override fun compareTo(other: EList): Int {
            return this.zip(other) { first, second ->
                when {
                    first is EInt && second is EInt -> first.compareTo(second)
                    first is EList && second is EList -> first.compareTo(second).takeUnless { it == 0 } ?: first.size.compareTo(second.size)
                    first is EList && second is EInt -> first.compareTo(EList(mutableListOf(second), second.parent))
                    first is EInt && second is EList -> EList(mutableListOf(first), first.parent).compareTo(second)
                    else -> throw IllegalStateException()
                }
            }.firstOrNull { result -> result != 0 } ?: this.size.compareTo(other.size)
        }

        override fun toString(): String {
            return list.toString()
        }
    }
}

private tailrec fun parseLine(line: String, currentList: Elem.EList, accumulatedInt: StringBuilder): Elem.EList {
    if (line.isEmpty()) return currentList

    val nextEntry = line.first()

    val newCurrent = when {
        nextEntry == '[' -> {
            val newList = Elem.EList(mutableListOf(), currentList)
            currentList.add(newList)
            newList
        }

        nextEntry == ']' -> {
            val maybeNumber = accumulatedInt.toString().toIntOrNull()
            accumulatedInt.clear()
            maybeNumber?.let { currentList.add(Elem.EInt(it, currentList)) }
            currentList.parent
        }

        nextEntry.isDigit() -> {
            accumulatedInt.append(nextEntry)
            currentList
        }

        nextEntry == ',' -> {
            val maybe = accumulatedInt.toString().toIntOrNull()
            accumulatedInt.clear()
            maybe?.let { currentList.add(Elem.EInt(it, currentList)) }
            currentList
        }

        else -> throw IllegalStateException()
    }

    val toParse = line.drop(1)

    return parseLine(toParse, newCurrent!!, accumulatedInt)
}