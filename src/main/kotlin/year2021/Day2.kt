package year2021

fun determinePosition(inputs: List<String>) : Int {

    val commands = inputs.map {
        when {
            it.startsWith("forward") -> Command.Forward(it.getAmount())
            it.startsWith("down") -> Command.Down(it.getAmount())
            it.startsWith("up") -> Command.Up(it.getAmount())
            else -> throw IllegalArgumentException()
        }
    }

    val lastPosition = commands.fold(Position1()){ position, command ->
        command.getNewPosition(position)
    }

    return lastPosition.forward * lastPosition.depth
}

private fun String.getAmount(): Int{
    val splitted = split(" ")
    return splitted[1].toInt()
}

private interface Command {
    fun getNewPosition(oldPosition: Position1): Position1

    class Forward(val howMuch: Int) : Command {
        override fun getNewPosition(oldPosition: Position1) = oldPosition.copy(
            forward = oldPosition.forward + howMuch,
            depth = oldPosition.depth + howMuch * oldPosition.aim
        )
    }

    class Down(val howMuch: Int) : Command {
        override fun getNewPosition(oldPosition: Position1) = oldPosition.copy(
            aim = oldPosition.aim + howMuch
        )
    }

    class Up(val howMuch: Int) : Command {
        override fun getNewPosition(oldPosition: Position1) = oldPosition.copy(
            aim = oldPosition.aim - howMuch
        )
    }
}

private data class Position1(val forward: Int = 0, val depth: Int = 0, val aim: Int = 0)