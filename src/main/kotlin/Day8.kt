fun day8(input: List<String>): Int {
    println("just input: $input")

    val displays = input.toDisplays()

    return displays.sumOf { display ->
        val number = display.displayedNumber
//        println("Display $display is in fact showing number: $number")
        number
    }
}

//private fun List<String>.justOutputs(): List<List<String>> = map { entry -> entry.justOutput() }
//
//private fun String.justOutput(): List<String> {
//    val output = split("|")[1]
//    return output.split(" ")
//}

private fun List<String>.toDisplays() = map { it.toDisplay() }

    private fun String.toDisplay(): Display {
        return split("|")
            .map { part -> part.trim().split(" ").map { it.toSet() } }
            .let { entry ->
//                println("entry: $entry")
                Display(DigitEncodings(entry.first()), EncodedOutcome(entry.last()))
            }
    }

//private enum class SegmentPosition { UPPER, UPPER_LEFT, UPPER_RIGHT, MIDDLE, LOWER_LEFT, LOWER_RIGHT, BOTTOM }
//
//private class Segment(val position: SegmentPosition, val encoding: Char)

private data class Digit(val segmentsIds: Set<Char>, val numericValue: Int)

private data class DigitEncodings(val values: List<Set<Char>>)

private class EncodedOutcome(val encodedDigits: List<Set<Char>>)

private data class Display(val encodings: DigitEncodings, val outcome: EncodedOutcome) {

    private val digits = getAllDigits().also { check(it.size == 10) }


    val displayedNumber: Int = outcome.encodedDigits
        .map { encodedDigit -> decodeValue(encodedDigit) }
        .joinToString(separator = "")
        .toInt()

    private fun getAllDigits(): Set<Digit> {
        println("encodings: $encodings")
        val one = findOne(encodings)
        val four = findFour(encodings)
        val seven = findSeven(encodings)
        val eight = findEight(encodings)
        val six = findSix(encodings, seven)
        val nine = findNine(encodings, four)
        val zero = findZero(encodings, six, nine)
        val two = findTwo(encodings, four)
        val three = findThree(encodings, one)
        val five = findFive(encodings, two, three)

        return setOf(zero, one, two, three, four, five, six, seven, eight, nine)
    }

    private fun decodeValue(encoded: Set<Char>): Int {
        return digits.filter { encoded == it.segmentsIds }.map { it.numericValue }.single()
    }
}

//private fun decodeUpperSegment(one: Digit, seven: Digit): Segment {
//    val upper = seven.segmentsIds.subtract(one.segmentsIds)
//    check(upper.size == 1)
//    return Segment(SegmentPosition.UPPER, upper.first())
//}

private fun findSeven(encodings: DigitEncodings) = encodings.values.single { it.size == 3 }.let { Digit(it, 7) }

//private fun decodeUpperRightSegment(six: Digit, seven: Digit): Segment {
//    val upperRight = seven.segmentsIds.subtract(six.segmentsIds)
//    check(upperRight.size == 1)
//    return Segment(SegmentPosition.UPPER_RIGHT, upperRight.first())
//
//}
//
//private fun decodeLowerRightSegment(upperLeftSegment: Segment, one: Digit): Segment {
//    val encoding = one.segmentsIds - upperLeftSegment.encoding
//    check(encoding.size == 1)
//    return Segment(SegmentPosition.LOWER_RIGHT, encoding.first())
//}

private fun findOne(encodings: DigitEncodings) = encodings.values
    .single { it.size == 2 }
    .let { Digit(it, 1) }

private fun findEight(encodings: DigitEncodings) = encodings.values
    .single { it.size == 7 }
    .let { Digit(it, 8) }

private fun findSixNineOrZero(encodings: DigitEncodings) = encodings.values.filter { it.size == 6 }

private fun findSix(encodings: DigitEncodings, seven: Digit) =
    findSixNineOrZero(encodings)
        .single { sixOrNineOrZero -> seven.segmentsIds.subtract(sixOrNineOrZero).size == 1 }
        .let { Digit(it, 6) }

private fun findNine(encodings: DigitEncodings, four: Digit) = findSixNineOrZero(encodings)
    .single { sixNineOrZero ->
        sixNineOrZero.intersect(four.segmentsIds).size == 4
//        val remainingSegment = sixNineOrZero.subtract(six.segmentsIds)
//        remainingSegment.size == 1 && remainingSegment.single() in one.segmentsIds
    }.let { Digit(it, 9) }

private fun findZero(encodings: DigitEncodings, six: Digit, nine: Digit) = findSixNineOrZero(encodings)
    .single { it != six.segmentsIds && it != nine.segmentsIds }
    .let { Digit(it, 0) }

private fun findTwoThreeOrFive(encodings: DigitEncodings) = encodings.values.filter { it.size == 5 }

private fun findFour(encodings: DigitEncodings) = encodings.values.single { it.size == 4 }.let { Digit(it, 4) }

private fun findTwo(encodings: DigitEncodings, four: Digit) = findTwoThreeOrFive(encodings)
    .single { twoThreeOrFive -> four.segmentsIds.intersect(twoThreeOrFive).size == 2 }
    .let { Digit(it, 2) }

private fun findThree(encodings: DigitEncodings, one: Digit) = findTwoThreeOrFive(encodings)
    .single { twoThreeOrFive ->
        twoThreeOrFive.intersect(one.segmentsIds).size == 2
    }.let { Digit(it, 3) }

private fun findFive(encodings: DigitEncodings, two: Digit, three: Digit) = findTwoThreeOrFive(encodings)
    .single { it != two.segmentsIds && it != three.segmentsIds }
    .let { Digit(it, 5) }

//private fun decodeBottomSegment(encodings: DigitEncodings): Segment {
//    val eight = encodings.values.first { it.size == 7 }
//    check(upper.size == 1)
//    return upper.first()
//}