package year2022

fun day6(data: List<String>): Int {

    return findFirstDistinct(data.first(), 14)
}

private fun findFirstDistinct(input: String, distinctCount: Int): Int {


    tailrec fun go(index: Int): Int {
        val substring = input.substring(index, index+distinctCount)
        val set = substring.toSet()

       return if(index > input.lastIndex) -1
        else if(set.size == distinctCount) index + distinctCount
        else go(index + 1)
    }

    return go(0)
}