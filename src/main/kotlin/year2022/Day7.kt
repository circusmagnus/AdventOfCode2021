package year2022

fun day7(data: List<String>): Int {
    val totalDiskSpace = 70000000
    val neededSpace = 30000000

    val all = data
        .onEach {  }
        .map { input ->
            println("step before parse is: $input")
            val result = if (input.startsWith("$")) input.drop(2).mapToCommand() else maybeToDir(input)
            result.also { println("step on creation is: $it") }
        }.onEach {  }

//    println("all things: $all")

    val root = Dir("/")
    buildTree(root, all, root, Command.ToRoot)

    val dirs = all.filterIsInstance<Dir>()
    val howMuchLeft = totalDiskSpace - root.getSize()
    val needToFree = neededSpace - howMuchLeft

    return dirs.filter{ !it.isFile() }.map { it.getSize() }.filter { it >= needToFree }.minOf { size -> size }
}

sealed interface Thing

sealed interface Command : Thing {
    object ToRoot : Command
    object ListStruct : Command
    data class GoTo(val name: String) : Command
    object GoUp : Command
}

private fun String.mapToCommand(): Command {

    return when {
        this.startsWith("cd /") -> Command.ToRoot
        this.startsWith("ls") -> Command.ListStruct
        this.startsWith("cd ..") -> Command.GoUp
        startsWith("cd ") -> {
            val dirName = split("cd ").last()
            Command.GoTo(dirName)
        }

        else -> throw IllegalStateException()
    }
}

private tailrec fun buildTree(root: Dir, steps: List<Thing>, currentDir: Dir, lastCommand: Command): Dir {
    println("step. step: ${steps.firstOrNull()}, currentDir: $currentDir, lastCOmmand: $lastCommand")


    return if (steps.isEmpty()) root
    else {
        val step = steps.first()
        when (step) {
            is Command -> {
                when (step) {
                    is Command.GoTo -> buildTree(
                        root,
                        steps.drop(1),
                        currentDir = currentDir.children.first { it.name == step.name },
                        lastCommand = step
                    )

                    Command.GoUp -> buildTree(root, steps.drop(1), currentDir = currentDir.parent!!, lastCommand = step)
                    Command.ListStruct -> buildTree(root, steps.drop(1), currentDir, step)
                    Command.ToRoot -> buildTree(root, steps.drop(1), root, step)
                }
            }

            is Dir -> {
                currentDir.children.add(step)
                step.parent = currentDir
                buildTree(root, steps.drop(1), currentDir, lastCommand)
            }
        }
    }


}

fun dirStringToDir(input: String): Dir {
    return input.split("dir ").last().let { Dir(name = it) }
}

fun fileStringToDir(input: String): Dir {
//    println("toFile: $input")
    val values = input.split(" ")
    val size = values.first()
    val name = values.last()
    return Dir(name = name, size = size.toInt())
}

fun maybeToDir(input: String): Dir = when {
    input.startsWith("dir") -> dirStringToDir(input)
    input.toCharArray().first().isDigit() -> fileStringToDir(input)
    else -> throw IllegalStateException()
}

data class Dir(val name: String, private val size: Int = 0) : Thing {
    var parent: Dir? = null
    val children = mutableSetOf<Dir>()

    fun isFile() = children.isEmpty()

    fun getSize(): Int = size + children.sumOf { it.getSize() }

    override fun toString(): String {
        return "Dir of $name, size: ${getSize()}, children: $children"
    }
}