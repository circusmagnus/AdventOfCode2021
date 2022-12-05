package year2022

fun day5(data: List<String>): String {
    val stacks = data
        .take(8)
        .fold(getInitialStacks()) { stacks, input ->
            getStackEntries(currentStacks = stacks, input = input)
        }.map { stack -> stack.reverse() }

    println("stacks: $stacks")

    val movements = data.drop(10)
        .map { it.getMovement() }



//    println("movemetns: $movements")

    movements.forEachIndexed { index, movement -> stacks.applyMovement(movement).also { println("stacks after movement $index: $stacks") } }

//    println("endingStacks: $stacks")


    return stacks.map { it.pop() }.joinToString(separator = "")
}

private fun List<Stack<Char>>.applyMovement(movement: Movement): List<Stack<Char>> {
    val from = this[movement.from]
    val to = this[movement.to]

    val tempStack = Stack<Char>()
    repeat(movement.amount) {
        tempStack.push(from.pop())
//        to.push(from.pop())
    }
    while (tempStack.size > 0) {
        to.push(tempStack.pop())
    }
    return this
}

private fun String.getMovement(): Movement {
    val amount = split(" from").first().filter { it.isDigit() }.toInt()
    val from = split("from ")[1].split(" to").first().toInt() - 1
    val to = split("to ").last().toInt() - 1

    return Movement(amount, from, to)

}

private data class Movement(val amount: Int, val from: Int, val to: Int)

private fun getInitialStacks(): List<Stack<Char>> =
    generateSequence { Stack<Char>() }.take(9).toList()

private fun getStackEntries(currentStacks: List<Stack<Char>>, input: String): List<Stack<Char>> {

    tailrec fun go(stackIndex: Int, inputIndex: Int) {
        if (stackIndex > currentStacks.lastIndex || inputIndex > input.lastIndex) return

        val symbol = input[inputIndex]
        if (symbol.isLetter()) currentStacks[stackIndex].push(symbol)

        go(stackIndex + 1, inputIndex + 4)
    }

    go(stackIndex = 0, inputIndex = 1)
    return currentStacks
}

private fun <T> Stack<T>.reverse(): Stack<T> {
    val newStack = Stack<T>()
    while (size > 0) {
        newStack.push(pop())
    }
    return newStack
}

class Stack<T>(private var input: MutableList<T> = mutableListOf()) {

    val size get() = input.size

    fun push(elem: T) {
        input.add(elem)
    }

    fun pop(): T = input.removeLast()

//    fun reverse() {
//        val newStack = mutableListOf<Char>()
//        for(i in input.lastIndex downTo 0) {
//            newStack.add(input[i])
//        }
//    }

    override fun toString(): String {
        return input.reversed().toString()
    }
}

