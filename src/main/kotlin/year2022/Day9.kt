package year2022

import plus

private const val mapSize = 1000
private const val startPos = 500

fun day9(data: List<String>): Int {
    val map = makeMap()

    val head = Head(startPos, startPos)
    val middle = buildList {
        repeat(8) { add(MiddleKnot(startPos, startPos)) }
    }
    val tail = Tail(MiddleKnot(startPos, startPos), map)
    val followers = middle + tail


    val movements = data.map { input ->
        val (dir, dist) = input.split(" ").let { (dirChar, distString) -> Pair(dirChar, distString.toInt()) }
        buildList {
            repeat(dist) {
                val move = when (dir) {
                    "L" -> Move.Left
                    "U" -> Move.Up
                    "R" -> Move.Right
                    "D" -> Move.Down
                    else -> throw IllegalStateException()
                }
                add(move)
            }
        }
    }.flatten()

    for (move in movements) {
        head.move(move)
        moveKnots(move, head, followers)
//        val drawableMap = makeDrawableMap(head + followers)
//        drawMap(drawableMap)
    }

    var visitedCount = 0

    for (column in map) {
        for (visited in column) {
            if (visited) visitedCount++
        }
    }

    return visitedCount
}


tailrec fun moveKnots(move: Move, after: WithPosition, knotsToMove: List<FollowingWithPosition>) {
    println("moving knots: move: $move, after: $after, follower: ${knotsToMove.firstOrNull()}")
    if (knotsToMove.isEmpty()) return
    else {
        knotsToMove.first().move(after)
        moveKnots(move, knotsToMove.first(), knotsToMove.drop(1))
    }
}

private fun makeDrawableMap(knots: List<WithPosition>): Array<CharArray> {
    return Array(1000) { y ->
        CharArray(1000) { x ->
            val maybeKnot = knots.firstOrNull { knot -> knot.posX == x && knot.posY == y }
            val symbol = maybeKnot?.let { knot ->
                when (knot) {
                    is Head -> 'H'
                    is Tail -> 'T'
                    is MiddleKnot -> 'M'
                    else -> throw IllegalStateException()
                }
            } ?: '.'
            symbol
        }
    }
}

private fun drawMap(map: Array<CharArray>) {
    for (x in 0..mapSize) {
        for(y in 0..mapSize) {
            print(map[x][y])
        }
        println("")
    }
}

sealed interface Move {
    object Left : Move
    object Up : Move
    object Right : Move
    object Down : Move
}

interface WithPosition {
    var posY: Int
    var posX: Int
}

interface Following {
    fun move(head: WithPosition)
}

interface FollowingWithPosition : Following, WithPosition

class Head(override var posY: Int, override var posX: Int) : WithPosition {

    fun move(how: Move) {
        when (how) {
            Move.Down -> posY += 1
            Move.Left -> posX -= 1
            Move.Right -> posX += 1
            Move.Up -> posY -= 1
        }
    }

    override fun toString(): String {
        return "Head, x: $posX, y: $posY"
    }
}

class Tail(val base: MiddleKnot, val map: Array<BooleanArray>) : FollowingWithPosition by base {

    init {
        map[posY][posX] = true
    }

    override fun move(head: WithPosition) {
        base.move(head)
        map[this.posY][posX] = true
        println("Tail moved to y: $posY, x: $posX")
    }

    override fun toString(): String {
        return "Tail, x: $posX, y: $posY"
    }
}

class MiddleKnot(override var posY: Int, override var posX: Int) : FollowingWithPosition {

    override fun move(head: WithPosition) {

        val xDist = head.posX - this.posX
        val yDist = head.posY - this.posY
        val isDiagonal = xDist != 0 && yDist != 0

        if (xDist < -1) {
            this.posX -= 1
            if (yDist < 0) this.posY-- else if(yDist > 0) posY++
        } else if (yDist < -1) {
            this.posY -= 1
            if (xDist < 0) this.posX-- else if(xDist > 0) posX++
        } else if (xDist > 1) {
            this.posX += 1
            if (yDist < 0) this.posY-- else if(yDist > 0) posY++

        } else if (yDist > 1) {
            this.posY += 1
            if (xDist < 0) this.posX-- else if(xDist > 0) posX++
        }
    }

    override fun toString(): String {
        return "Middle, x: $posX, y: $posY"
    }
}

fun makeMap(): Array<BooleanArray> {
    return Array(mapSize) { yIndex ->
        BooleanArray(mapSize) { xIndex ->
            false
        }
    }
}