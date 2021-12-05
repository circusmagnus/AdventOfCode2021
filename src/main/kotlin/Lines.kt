import java.io.File

fun getData(filePath: String): List<String> = File(filePath).useLines { sequence ->
    sequence.toList()
}