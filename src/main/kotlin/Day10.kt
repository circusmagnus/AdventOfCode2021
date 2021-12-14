import java.util.*
import kotlin.collections.ArrayDeque

fun day10(input: List<String>): Long {

    val sortedResults =  input.filter { line -> parseWithTags(line, ArrayDeque()) == null }
        .map { uncompleteLine -> completeTags(uncompleteLine, ArrayDeque()) }
        .sorted()

    val median = sortedResults[sortedResults.lastIndex / 2]
//        .sumOf { illegalChar -> illegalChar.toPoints() }

    return median

}

private tailrec fun completeTags(toComplete: String, openedTags: List<Char>): Long {
    if (toComplete.isEmpty()) {
        val addedTags = openedTags.map { it.closingTag() }.reversed().joinToString(separator = "")
        return calculateScore(addedTags)
    }
    val nextChar = toComplete.first()

//    println("parsing: openingChars: $openingChars, nextChar: $nextChar")

    return when (nextChar) {
        '(',
        '[',
        '{',
        '<' -> {
            completeTags(toComplete.drop(1), openedTags + nextChar)
        }
        ')',
        ']',
        '}',
        '>' -> {
            if(openedTags.isEmpty()) {
                val addedTags = openedTags.reversed().joinToString(separator = "")
                return calculateScore(addedTags)
            }
            else completeTags(toComplete.drop(1), openedTags.dropLast(1))
        }
        else -> throw IllegalStateException()
    }

}

private fun calculateScore(remainingTags: String) : Long {
    println("calculatingScore for $remainingTags")
    val score =  remainingTags
        .fold(0L) { score, char ->
            val multipliedScore = score * 5
            val addedValue = when(char){
                ')' -> 1
                ']' -> 2
                '}' -> 3
                '>' -> 4
                else -> throw IllegalStateException()
            }
            multipliedScore + addedValue
        }
    println("score: $score")
    return score
}



//{
//    if(remainingTags.isEmpty()) return currentScore
//    val multipliedScore = currentScore * 5
//    val tag = remainingTags.first()
//    val addedValue = when(tag){
//        ')' -> 1
//        ']' -> 2
//        '}' -> 3
//        '>' -> 4
//        else -> throw IllegalStateException()
//    }
//    val newScore = multipliedScore + addedValue
//    return calculateScore(remainingTags.drop(1), newScore)
//}

private tailrec fun parseWithTags(toParse: String, openingChars: List<Char>): Char? {
    if (toParse.isEmpty()) return null
    val nextChar = toParse.first()

//    println("parsing: openingChars: $openingChars, nextChar: $nextChar")

    return when (nextChar) {
        '(',
        '[',
        '{',
        '<' -> {
            parseWithTags(toParse.drop(1), openingChars + nextChar)
        }
        ')',
        ']',
        '}',
        '>' -> {
            if(openingChars.isEmpty()) nextChar
            else if(openingChars.last().closingTag() == nextChar) parseWithTags(toParse.drop(1), openingChars.dropLast(1)) else nextChar
        }
        else -> throw IllegalStateException()
    }
}

private fun Char.closingTag() = when (this) {
    '(' -> ')'
    '[' -> ']'
    '{' -> '}'
    '<' -> '>'
    else -> throw IllegalStateException()
}

private fun Char.toPoints(): Int = when (this) {
    ')' -> 3
    ']' -> 57
    '}' -> 1197
    '>' -> 25137
    else -> 0
}