fun determinePosition(inputs: List<String>) : Int {

    val commands = inputs.map {
        when {
            it.startsWith("forward") -> Command.Forward(it.getAmount())
            it.startsWith("down") -> Command.Down(it.getAmount())
            it.startsWith("up") -> Command.Up(it.getAmount())
            else -> throw IllegalArgumentException()
        }
    }

    val lastPosition = commands.fold(Position()){ position, command ->
        command.getNewPosition(position)
    }

    return lastPosition.forward * lastPosition.depth
}

fun String.getAmount(): Int{
    val splitted = split(" ")
    return splitted[1].toInt()
}

interface Command {
    fun getNewPosition(oldPosition: Position): Position

    class Forward(val howMuch: Int) : Command{
        override fun getNewPosition(oldPosition: Position) = oldPosition.copy(
            forward = oldPosition.forward + howMuch,
            depth = oldPosition.depth + howMuch * oldPosition.aim
        )
    }

    class Down(val howMuch: Int) : Command{
        override fun getNewPosition(oldPosition: Position) = oldPosition.copy(
            aim = oldPosition.aim + howMuch
        )
    }

    class Up(val howMuch: Int) : Command {
        override fun getNewPosition(oldPosition: Position) = oldPosition.copy(
            aim = oldPosition.aim - howMuch
        )
    }
}

data class Position(val forward: Int = 0, val depth: Int = 0, val aim: Int = 0)