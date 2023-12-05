import java.io.File

fun getData(filePath: String): List<String> = File(filePath).useLines { sequence ->
    sequence.toList()
}

inline fun List<String>.splitBy(shouldSplit: (line: String) -> Boolean): List<List<String>> {
    val externalList = this
    return buildList {
        val acc = mutableListOf<String>()

        for (line in externalList) {
//            println("adding line from external list: $line. should Split: ${shouldSplit(line)}")
            if (shouldSplit(line)) {
                if (acc.isNotEmpty()) {
                    add(acc.toList())
                }
                acc.clear()
            } else {
                acc.add(line)
            }
        }

        add(acc)
    }
}