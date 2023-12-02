package year2023

/**
 * Podzielic gre na sety
 * spr, czy ktorys set przekracza max dla ktoregos koloru
 */

private const val MAX_RED = 12
private const val MAX_GREEN = 13
private const val MAX_BLUE = 14
private const val BLUE = "blue"
private const val RED = "red"
private const val GREEN = "green"
fun day2(input: List<String>): Int {
    return input.sumOf { game ->
        possibleIdOrZero(game)
    }
}

data class Game(val id: Int, val maxBlue: Int, val mxRed: Int, val maxGreen: Int)
data class GameSet(val red: Int,  val blue: Int, val green: Int)

fun possibleIdOrZero(input: String): Int {
    val gameSets = input.split(":")[1]
        .split(';')
        .map { setEntry ->
            setEntry.split(',').map { cubeEntry ->
                val amount = cubeEntry.filter { it.isDigit() }.toInt()
                val color = when {
                    BLUE in cubeEntry -> BLUE
                    RED in cubeEntry -> RED
                    GREEN in cubeEntry -> GREEN
                    else -> throw IllegalStateException()
                }
                Pair(color, amount)
            }
        }.map { set -> 
            val blue =  set.filter { it.first == BLUE }.firstOrNull()
            val red = set.filter { it.first == RED }.firstOrNull()
            val green = set.filter { it.first == GREEN }.firstOrNull()
            
            GameSet(red = red?.second ?: 0, blue = blue?.second ?: 0, green = green?.second ?: 0)
        }

    val maxBlue = gameSets.maxBy { set -> set.blue }.blue
//    if (maxBlue > MAX_BLUE) return 0

    val maxGreen = gameSets.maxBy { set -> set.green }.green
//    if (maxGreen > MAX_GREEN) return 0

    val maxRed = gameSets.maxBy { set -> set.red }.red
//    if (maxRed > MAX_RED) return 0

    val id = input.split(':').first().split("Game ").last().toInt()

    return maxBlue * maxRed * maxGreen
}