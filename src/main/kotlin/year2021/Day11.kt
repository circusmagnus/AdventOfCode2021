package year2021

fun day11(input: List<String>): Int {
    val (map, octi) = buildMapAndOcti(input)

    octi.forEach { it.establishNeighbours(map) }

    for (step in 1..100_000_000) {
        for (line in map) {
            for (octopus in line) {
                print("${octopus.energy}")
            }
            println("")
        }

        if(octi.all { it.hasFlashed }) return step - 1

        octi.forEach { it.onStep() }
        octi.forEach { it.flashIfCharged() }
        println("")
    }


    return octi.sumOf { it.flashCount }
}

private fun buildMapAndOcti(input: List<String>): Pair<Array<Array<FlashingOctopus>>, List<FlashingOctopus>> {
    val octi = mutableListOf<FlashingOctopus>()

    val map = Array(10) { y ->
        Array(10) { x ->
            FlashingOctopus(x = x, y = y, initialEnergy = input[y][x].digitToInt())
                .also { octi.add(it) }
        }
    }

    return Pair(map, octi)
}

private class FlashingOctopus(val x: Int, val y: Int, initialEnergy: Int) {

    var energy: Int = initialEnergy
        private set

    val isAboutToFlash get() = energy >= 10
    val hasFlashed get() = energy == 0

    var flashCount = 0
        private set

    private var upper: FlashingOctopus? = null
    private var upperRight: FlashingOctopus? = null
    private var right: FlashingOctopus? = null
    private var lowerRight: FlashingOctopus? = null
    private var lower: FlashingOctopus? = null
    private var lowerLeft: FlashingOctopus? = null
    private var left: FlashingOctopus? = null
    private var upperLeft: FlashingOctopus? = null

    private fun getNeighbours() =
        setOf(upper, upperRight, right, lowerRight, lower, lowerLeft, left, upperLeft).filterNotNull()


    fun establishNeighbours(map: Array<Array<FlashingOctopus>>) {
        upper = if (hasUpperNeighbour) map[y - 1][x] else null
        upperRight = if (hasUpperRightNeighbour(map)) map[y - 1][x + 1] else null
        right = if (hasRightNeighbour(map)) map[y][x + 1] else null
        lowerRight = if (hasLowerRightNeighbour(map)) map[y + 1][x + 1] else null
        lower = if (hasLowerNeighbour(map)) map[y + 1][x] else null
        lowerLeft = if (hasLowerLeftNeighbour(map)) map[y + 1][x - 1] else null
        left = if (hasLeftNeighbour) map[y][x - 1] else null
        upperLeft = if (hasUpperLeftNeighbour()) map[y - 1][x - 1] else null
    }

    fun onStep() {
//        println("octopus $this with energy $energy is getting more energy on step")
        energy++
    }

    fun onNearbyFlash() {
        if (!hasFlashed) {
//            println("octopus $this which has not flashed, with energy $energy is getting more energy due to neighbour flashing")
            energy++
            flashIfCharged()
        }
    }

    fun flashIfCharged() {
        if (isAboutToFlash) {
//            println("octopus $this who is about to flash, with energy $energy is flashing")
            flashCount++
            energy = 0
            getNeighbours().forEach { it.onNearbyFlash() }
        }
    }

    private val hasLeftNeighbour get() = x > 0
    private val hasUpperNeighbour get() = y > 0
    private fun hasLowerNeighbour(map: Array<Array<FlashingOctopus>>) = y < map.lastIndex
    private fun hasRightNeighbour(map: Array<Array<FlashingOctopus>>) = x < map.first().lastIndex

    private fun hasUpperRightNeighbour(map: Array<Array<FlashingOctopus>>) = hasUpperNeighbour && hasRightNeighbour(map)
    private fun hasLowerRightNeighbour(map: Array<Array<FlashingOctopus>>) =
        hasRightNeighbour(map) && hasLowerNeighbour(map)

    private fun hasLowerLeftNeighbour(map: Array<Array<FlashingOctopus>>) = hasLowerNeighbour(map) && hasLeftNeighbour
    private fun hasUpperLeftNeighbour() = hasLeftNeighbour && hasUpperNeighbour
}