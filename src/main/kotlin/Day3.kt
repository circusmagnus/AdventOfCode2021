fun calculatePowerConsumption(data: List<String>): Int {
    val withInts = data.asSequence().map { row -> row.map { it.digitToInt() }.map { if (it == 1) 1 else -1 } }
    val initialArray = IntArray(data[0].length) { 0 }
    val finalGammeBits = withInts.fold(initialArray) { acc, list ->
        list.forEachIndexed { index, value -> acc[index] = acc[index] + value }
        acc
    }.map { cellValue -> if (cellValue > 0) 1 else if (cellValue < 0) 0 else throw IllegalStateException() }

    val finalEpsilonBits = finalGammeBits.map { cellValue -> if (cellValue == 1) 0 else 1 }

    println("finalGammaBits: $finalGammeBits")
    println("finalEpsilonBits: $finalEpsilonBits")

    val gammaDecimal = finalGammeBits.joinToString(separator = "").toInt(2)
    val epsilonDecimal = finalEpsilonBits.joinToString(separator = "").toInt(2)


    return gammaDecimal * epsilonDecimal
}

fun day3(data: List<String>): Int {
    val cellSize = data.first().length
    val withInts = data.map { row -> row.map { it.digitToInt() } }

    tailrec fun reduce(table: List<List<Int>>, index: Int, mostCommon: Boolean): List<Int> {
        return if (table.size == 1) {
            table.first()
        }
        else {
            val sum = table.asSequence()
                .map { it[index] }
                .map { value -> if (value == 1) 1 else -1 }
                .fold(0) { acc, i -> acc + i }
            val oxygenOutcome = if (sum >= 0) 1 else 0
            val co2Outcome = if(sum < 0 ) 1 else if( sum > 0) 0 else 0
            val finalOutcome = if(mostCommon) oxygenOutcome else co2Outcome
            val reducedTable = table.filter { it[index] == finalOutcome }
            reduce(reducedTable, index = index + 1, mostCommon)
        }
    }

    val oxygen = reduce(withInts, 0, mostCommon = true)
    println("oxygen: $oxygen")
    val oxygenDecimal = oxygen.joinToString(separator = "").toInt(2)
    val co2 = reduce(withInts, 0, mostCommon = false)
    println("co2: $co2")
    val co2Decimal = co2.joinToString(separator = "").toInt(2)


    return oxygenDecimal * co2Decimal
}

//private fun getColumns(data: List<String>) : List<List<Int>> {
//
//    tailrec fun getColumn(toRead: List<String>, current: List<String>): List<String> {
//        return if()
//    }
//}
//
//private fun gammaForColumn(bits: List<Int>): Int {
//    val sumByValue = bits.sum()
//    val isOne = (sumByValue * 2) > bits.size
//    val isZero = (sumByValue * 2) < bits.size
//
//    return if (isOne) 1
//    else if (isZero) 0
//    else throw IllegalStateException()
//}