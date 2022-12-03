package year2022

fun day3(data: List<String>): Int {

    val result = data.asSequence()
//        .map { rucksack -> getCompartments(rucksack) }
        .chunked(3)
        .map { groupRucksacks -> findBadgeInAGroup(groupRucksacks) }
        .onEach { dupe -> println("badge: $dupe") }
        .map { letter -> findValue(letter).also { println("letter: $letter, code: $it") } }
        .sum()


    return result
}

private fun findValue(char: Char) = when{
    char.isUpperCase() -> char.code - 38
    char.isLowerCase() -> char.code - 96
    else  -> throw IllegalStateException()
}

private fun findBadgeInAGroup(compartments: List<String>): Char {

    val (first, second, third) = compartments
    val firstAndSecond = first.toSet().intersect(second.toSet())
    val withThird = firstAndSecond.intersect(third.toSet())
    return withThird.first()

}

private fun getCompartments(rucksack: String): Pair<String, String> {
    val compSize = rucksack.length / 2
    return Pair(rucksack.take(compSize), rucksack.drop(compSize))
}