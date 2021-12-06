fun day6(input: List<String>): Long {
    val initialFishes = getInitialFishes(input)

    tailrec fun passTimeAndCountFishes(fishes: MutableMap<Int, Long>, day: Int): Long {
        return if (day == 256) fishes.values.sum()
        else {
            val newFishes = getFishesMap()
            fishes.forEach { (fishStage, fishCount) ->
                when(fishStage){
                    in 1..8 -> newFishes[fishStage - 1] = newFishes[fishStage - 1]!! + fishCount
                    0 -> {
                        newFishes[6] = newFishes[6]!! + fishCount
                        newFishes[8] = newFishes[8]!! + fishCount
                    }
                }
            }
            passTimeAndCountFishes(newFishes, day + 1)
        }
    }

    return passTimeAndCountFishes(initialFishes, 0)
}

@OptIn(ExperimentalStdlibApi::class)
private fun getInitialFishes(input: List<String>): MutableMap<Int, Long> {
    val initialFishes = input.first().split(",").map { it.toInt() }
    val map = getFishesMap()

    initialFishes.forEach { fishStage -> map[fishStage] = map[fishStage]!! + 1 }
    return map
}

private fun getFishesMap(): MutableMap<Int, Long>{
    val map = mutableMapOf<Int, Long>()
    for (i in 8 downTo 0) {
        map[i] = 0
    }
    return map
}