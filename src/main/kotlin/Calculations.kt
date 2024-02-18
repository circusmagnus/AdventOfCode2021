fun greatestCommonDivisor(a: Long, b: Long): Long {
    if (b == 0L) return a
    return greatestCommonDivisor(b, a % b)
}

fun leastCommonMultiple(a: Long, b: Long): Long {
    return a * b / greatestCommonDivisor(a, b)
}

fun leastCommonMultiple(numbers: List<Long>): Long {
    var result = numbers[0]
    for (i in 1 until numbers.size) {
        result = leastCommonMultiple(result, numbers[i])
    }
    return result
}