package year2022

fun day10(data: List<String>): Int {

    val instructions = toInstr(data)
    val proc = Processor()

        return proc.submit(instructions)
}

private class Processor {

    fun submit(instr: List<Instr>): Int {
        val upTo = 220
        val after20 = runInstr(instr, State(cycle = 1, x = 1, finished = true), upToCycle = upTo, previousResults = emptyList())
        println("processor result after $upTo: $after20")
        return after20.sum()
    }
}

private tailrec fun runInstr(instr: List<Instr>, currentState: State, upToCycle: Int, previousResults: List<Int>): List<Int> {
    val shouldCheck = currentState.cycle == 20 || (currentState.cycle - 20) % 40 == 0
    val newResult = if(shouldCheck) previousResults + (currentState.x * currentState.cycle) else previousResults

    println("iteration with state $currentState and next instr: ${instr.firstOrNull()}")

    println("new result after cycle ${currentState.cycle} and current value of x: ${currentState.x} is $newResult")

    return if(instr.isEmpty()) newResult
    else if(currentState.cycle == upToCycle) newResult
    else {
        val currentInstr = instr.first()
        val newState = currentInstr.run(currentState)
        val instrToRunNext = if(newState.finished) instr.drop(1) else instr
        runInstr(instrToRunNext, newState, upToCycle, newResult)
    }
}

private fun toInstr(input: List<String>) = input.map { string ->
    val raw = string.split(" ")
    val type = if (raw.first() == "noop") Noop else AddX(raw.last().toInt())
    type
}

private data class State(val finished: Boolean, val x: Int, val cycle: Int)

private interface Instr {

//    var timeSpent: Int

    fun run(state: State): State
}

private object Noop : Instr {
    override fun run(state: State): State {
        return state.copy(finished = true, cycle = state.cycle + 1)
    }
}

private class AddX(private val value: Int) : Instr {

    var innerCycle = 0

    override fun run(state: State): State {
        innerCycle++
        val finished = innerCycle == 2
        val newX = if (finished) state.x + value else state.x
//        println("intruction $this, innerCycle at $innerCycle, finished: $finished")
        return State(finished = finished, x = newX, cycle = state.cycle + 1)
    }

    override fun toString(): String {
        return "AddX($value)"
    }
}