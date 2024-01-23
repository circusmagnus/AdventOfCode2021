
import year2023.*
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

@OptIn(ExperimentalTime::class)
@ExperimentalStdlibApi
fun main() {

//    println("answer: ${day5(getData("src/main/resources/year2023/day05"))}")

    val measured = measureTimedValue { day7(getData("src/main/resources/year2023/day07")) }
    println("task done in ${measured.duration} with value: ${measured.value}")


}