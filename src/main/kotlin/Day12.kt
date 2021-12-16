fun day12(input: List<String>): Int {

    val caves = makeCaveSystem(input)

    val start = caves["start"]!!

    val onlyPlainSmallCaves =
        caves.filterKeys { name -> name != "start" && name != "end" && name.all { it.isLowerCase() } }.values.toList()

    val allPaths = mutableSetOf<Path>()

    for (i in onlyPlainSmallCaves.indices) {
        onlyPlainSmallCaves[i].permittedVisits = 2
        val paths = start.findPaths(emptyList())
        allPaths.addAll(paths)
        onlyPlainSmallCaves[i].permittedVisits = 1

    }

    println("allPaths: ")
    for (path in allPaths) println(path)

    return allPaths.size
}


private fun makeCaveSystem(input: List<String>): Map<String, Cave> {
    val caves = mutableMapOf<String, Cave>()

    input.map { line ->
        val connectedCavesIds = line.split("-")
        val connectedCaves = connectedCavesIds.map { caves[it] ?: Cave.fromString(it) }
        check(connectedCaves.size == 2)
        println("connectedCaves: $connectedCaves")
        connectedCaves.forEach { caves[it.name] = it }
        connectedCaves.first().addNeighbour(connectedCaves.last())
        connectedCaves.last().addNeighbour(connectedCaves.first())
    }

    println("Cave system: ${caves.values}")
    check(caves["start"] != null && caves["end"] != null)

    return caves
}

typealias Path = List<Cave>

class Cave(
    val isEnd: Boolean,
    val name: String,
    var permittedVisits: Int
) {


    private val neighbours: MutableSet<Cave> = mutableSetOf()


    fun addNeighbour(neighbour: Cave) {
        neighbours.add(neighbour)
    }

    fun findPaths(pathSoFar: Path): List<Path> {
        if (this.isEnd) return listOf(pathSoFar + this)

        val canVisit = pathSoFar.count { cave -> cave == this } < permittedVisits

        return when {
            !canVisit -> listOf(pathSoFar)
            else -> {
                val updatedWithCurrent = pathSoFar + this
                neighbours.map { neighbour -> neighbour.findPaths(updatedWithCurrent) }
                    .flatten()
                    .filter { it.isNotEmpty() && it.last().isEnd }
            }
        }
    }

    override fun toString(): String = name


    companion object {
        fun fromString(what: String): Cave {
            return when {
                what == "start" -> Cave(isEnd = false, name = what, permittedVisits = 1)
                what == "end" -> Cave(isEnd = true, name = what, permittedVisits = 1)
                what.all { it.isLowerCase() } -> Cave(isEnd = false, name = what, permittedVisits = 1)
                what.all { it.isUpperCase() } -> Cave(isEnd = false, name = what, permittedVisits = Int.MAX_VALUE)
                else -> throw IllegalStateException()
            }
        }
    }
}