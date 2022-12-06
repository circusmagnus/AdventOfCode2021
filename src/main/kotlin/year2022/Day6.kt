package year2022

fun day6(data: List<String>): Int {

    return findFirstFour(data.first())
}

private fun findFirstFour(input: String): Int {


    tailrec fun go(index: Int): Int {
        val substring = input.substring(index, index+14)
        val set = substring.toSet()

       return if(index > input.lastIndex) return -1
        else if(set.size == 14) return index + 14
        else go(index + 1)
    }

    return go(0)
}