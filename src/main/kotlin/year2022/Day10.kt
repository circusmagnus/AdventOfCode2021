package year2022

fun day10(data: List<String>): Int {

    val instructions = toInstr(data)
    val proc = Processor()

    return proc.submit(instructions)
}

private class Processor {

    val screen = Array(6) { y ->
        CharArray(40) { x ->
            '.'
        }
    }

    private fun draw(screen: Array<CharArray>) {
        for (row in screen) {
            for (char in row) {
                print(char)
            }
            println()
        }
    }

    fun submit(instr: List<Instr>): Int {
        val upTo = 239
        val after20 = runInstr(instr, State(cycle = 0, x = 1, finished = true), upToCycle = upTo, screen)
        draw(after20)
        return 0
    }
}

private tailrec fun runInstr(
    instr: List<Instr>,
    currentState: State,
    upToCycle: Int,
    screen: Array<CharArray>
): Array<CharArray> {
    val drawnRange = (currentState.x - 1)..(currentState.x + 1)
    val yPosition = currentState.cycle / 40
    val xPosition = currentState.cycle % 40

    println("drawing: y: $yPosition, x: $xPosition, drawnRange: $drawnRange")
    if (xPosition in drawnRange) screen[yPosition][xPosition] = '#'
    else screen[yPosition][xPosition] = '.'


    return if (instr.isEmpty()) screen
    else if (currentState.cycle == upToCycle) screen
    else {
        val currentInstr = instr.first()
        val newState = currentInstr.run(currentState)
        val instrToRunNext = if (newState.finished) instr.drop(1) else instr
        runInstr(instrToRunNext, newState, upToCycle, screen)
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