package year2021

import putOrIncrement

fun day14(input: List<String>): Long  {

    val rules = findRules(input)
    val template = findTemplate(input)
    val initialRuleCount = fillRulesWithTemplate(template, rules)
    val occurences = findOccurences(template)



    tailrec fun grow(step: Int, currentRuleCount: RuleCount): RuleCount {
        if(step == 40) return currentRuleCount
        val newRuleCount = mutableMapOf<Rule, Long>()

        val existingRules = currentRuleCount.filterValues { it != 0L }

        for (rule in existingRules){
            val (firstChild, secondChild) = rule.key.getChildren(rules)
            newRuleCount.putOrIncrement(firstChild, rule.value)
            newRuleCount.putOrIncrement(secondChild, rule.value)

            occurences.putOrIncrement(rule.key.insert, rule.value)
        }
        return grow(step + 1, newRuleCount)
    }

    val outcomeRuleCount = grow(0, initialRuleCount)

    val mostCommonOccurences = occurences.maxByOrNull { it.value }!!.also { println("most common is ${it.key} with value of: ${it.value}") }.value
    val leastCommon = occurences.minByOrNull { entry -> entry.value }!!.also { println("least common is ${it.key} with value of: ${it.value}") }.value


    return (mostCommonOccurences!! - leastCommon!!)
}

private fun findOccurences(template: String): MutableMap<Char, Long> {
    val map = mutableMapOf<Char, Long>()
    template.forEach { char ->
        map.putOrIncrement(char, 1)
    }
    return map
}

private fun fillRulesWithTemplate(template: String, rules: Rules): RuleCount {
    val ruleCount = mutableMapOf<Rule, Long>()
    template
        .windowed(size = 2, step = 1)
        .onEach { window ->
            val firstChar = window.first()
            val secondChar = window.last()
            val rule = rules.first { it.require.first == firstChar && it.require.second == secondChar }
            ruleCount[rule] = (ruleCount[rule] ?: 0) + 1
        }
    return ruleCount
}

private fun findTemplate(input: List<String>): String {
    return input.first()
}

typealias Rules = Set<Rule>

typealias RuleCount = MutableMap<Rule, Long>

data class Rule(val require: Pair<Char, Char>, val insert: Char) {

    fun getChildren(rules: Rules): Pair<Rule, Rule> {
        val firstChildRule = rules.first { it.require.first == this.require.first && it.require.second == insert }
        val secondChildRule = rules.first { it.require.first == insert && it.require.second == this.require.second }

        return firstChildRule to secondChildRule
    }
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