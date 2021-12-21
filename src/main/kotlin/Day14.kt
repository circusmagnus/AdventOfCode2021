import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.math.BigInteger

fun day14(input: List<String>): Long = runBlocking(Dispatchers.Default) {

    val rules = findRules(input)
    val template = findTemplate(input)
    val occurences = mutableMapOf<Char, BigInteger>()
    template.forEach { char -> occurences[char] = (occurences[char] ?: BigInteger.ZERO).add(BigInteger.ONE) }

    val motherRules = findMotherRules(template, rules, occurences)

    motherRules.first().let { it.countChars() }


//    rules.values.forEach { c: Char -> if (occurences[c] == null) occurences[c] = 0 }

//    val nodes = getMotherNodes(template, rules, occurences)
//
//    nodes.map { node ->
//        listOf('B', 'H').forEach { key -> node.countCreated(key).let { occurences[key] = occurences[key]!! + it } }
//    }
//        .count()


//    tailrec fun growStep2(oldTemplate: StringBuilder, currentTemplate: StringBuilder): StringBuilder {
//        if (oldTemplate.length == 1) {
//            currentTemplate.append(oldTemplate.last())
//            return currentTemplate
//        }
//        val nextPair = oldTemplate.first() to oldTemplate[1]
//        val toInsert = rules[nextPair]
//       val new = toInsert?.let { insert ->
//            occurences[insert] = (occurences[insert] ?: 0) + 1
//           currentTemplate.append(nextPair.first).append(insert)
////            newTemplate + insert
//        } ?: (currentTemplate.append(nextPair.first))
//        return growStep2(oldTemplate.deleteCharAt(0), new)
//    }
//
//    tailrec fun growProcess(step: Int, template: StringBuilder): StringBuilder {
////        println("template after step $step is: ${template}")
//        return if (step == 40) template
//        else {
//            val newTemplate = growStep2(template, StringBuilder(""))
//            growProcess(step + 1, newTemplate)
//        }
////        else {
////            val iterator = template.listIterator()
////            iterator.growStep3(rules, occurences)
////            growProcess(step + 1, template)
////        }
//    }
//
//    val grown = growProcess(0, StringBuilder(template))
//
    val mostCommonOccurences = occurences.maxByOrNull { entry -> entry.value }!!.value
    val leastCommon = occurences.minByOrNull { entry -> entry.value }!!.value


    (mostCommonOccurences!! - leastCommon!!).toLong()
}

fun findMotherRules(template: String, rules: Rules, occurences: MutableMap<Char, BigInteger>): List<MotherRule>{
     return template.windowed(size = 2, step = 1)
         .map { MotherRule(rules, occurences, it.first(), it.last()) }
}

class MotherRule(val rules: Map<Rule, Long>, val occurences: MutableMap<Char, BigInteger>, val motherFirst: Char, val motherSecond: Char) {


    fun countChars() {
        countChars(0, first = motherFirst, second = motherSecond, occurences)
    }

    private fun countChars(step: Int, first: Char, second: Char, rules: MutableMap<Rule, Long>, occurences: MutableMap<Char, BigInteger>) {
        if (step == 30) return
        val currentRule = rules
            .keys
            .firstOrNull { first == it.require.first && second == it.require.second }
            ?: return

        val insertedChars = rules[currentRule]!!
        val firstChildRule = rules
            .keys
            .first { it.require.first == first && it.require.second == currentRule.insert }

        rules[firstChildRule] = insertedChars

        occurences[currentRule.insert] = (occurences[currentRule.insert] ?: BigInteger.ZERO) + BigInteger.ONE
        countChars(step + 1, first = first, second = currentRule.insert, occurences)
        countChars(step + 1, first = currentRule.insert, second = second, occurences)

    }
}

//private fun MutableListIterator<Char>.growStep3(rules: Rules, occurences: MutableMap<Char, Long>) {
//    while (hasNext()) {
//        val first = next()
//        val second = if (hasNext()) next() else return
////        println("first: $first second: $second")
//        val maybeInsert = rules[first to second]
//        maybeInsert?.let { insert ->
//            occurences[insert] = (occurences[insert] ?: 0) + 1
//            previous()
//            add(insert)
//        }
//    }
//}

private fun findTemplate(input: List<String>): String {
    return input.first()
}

typealias Rules = Set<Rule>

data class Rule(val require: Pair<Char, Char>, val insert: Char) {
}

private fun findRules(input: List<String>): Rules {
    val justRules = input.drop(2)
    return justRules.map { line ->
        val splitted = line.split("->").map { it.trim() }
        val charToInsert = splitted.last().single()
        val require = splitted.first().first() to splitted.first().last()
        Rule(require, charToInsert)
    }.toSet()
}
//
//class Node(
//    val level: Int,
//    val first: Char,
//    val second: Char,
//    val rules: Rules,
//    val occurences: MutableMap<Char, Long>
//) {
//
//    companion object {
//        const val MAX_LEVEL = 30
//    }
//
//    val maybeInserted = if (level <= MAX_LEVEL) rules[first to second] else null
//
//    val firstChild = maybeInserted?.let { Node(level + 1, first, it, rules, occurences) }
//    val secondChild = maybeInserted?.let { Node(level + 1, it, second, rules, occurences) }
//    val children = listOfNotNull(firstChild, secondChild)
//
//
////    init {
////        maybeInserted?.let { occurences[it] = (occurences[it] ?: 0) + 1 }
////    }
//
//    fun countCreated(polimer: Char): Long {
//
//
//        val self = when {
//            polimer == maybeInserted -> {
////                occurences[polimer] = (occurences[polimer] ?: 0) + 1
//                1
//            }
//            else -> 0
//        }
//        return self + children.sumOf { it.countCreated(polimer) }
//    }
//
//}

//private fun getMotherNodes(template: String, rules: Rules, occurences: MutableMap<Char, Long>): Flow<Node> {
//    return template.asSequence()
//        .windowed(size = 2, step = 1)
//        .asFlow()
//        .map { Node(1, it.first(), it.last(), rules, occurences) }
//}