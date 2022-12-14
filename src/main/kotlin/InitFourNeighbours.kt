interface WithFourNeighbours<T : WithFourNeighbours<T>> {
    var left: T?
    var top: T?
    var right: T?
    var bottom: T?
}

interface WithEightNeighbours<T : WithEightNeighbours<T>> : WithFourNeighbours<T> {
    var topLeft: T?
    var topRight: T?
    var bottomRight: T?
    var bottomLeft: T?
}

fun <T : WithFourNeighbours<T>> Array<Array<T>>.initFourNeighbours() {

    for (y in this.indices) {
        for (x in this.first().indices) {
            val point = this[y][x]
            point.left = if (x > 0) this[y][x - 1] else null
            point.top = if (y > 0) this[y - 1][x] else null
            point.right = if (x < this.first().lastIndex) this[y][x + 1] else null
            point.bottom = if (y < this.lastIndex) this[y + 1][x] else null
        }
    }
}

fun <T : WithEightNeighbours<T>> Array<Array<T>>.initEightNeighbours() {

    initFourNeighbours()
    for (y in this.indices) {
        for (x in this.first().indices) {
            val point = this[y][x]
            point.topLeft = if (x > 0 && y > 0) this[y - 1][x - 1] else null
            point.topRight = if (x < this.first().lastIndex && y > 0) this[y - 1][x + 1] else null
            point.bottomRight = if (x < this.first().lastIndex && y < this.lastIndex) this[y + 1][x + 1] else null
            point.bottomLeft = if (x > 0 && y < this.lastIndex) this[y + 1][x - 1] else null
        }
    }
}

interface WithPosition {
    val x: Int
    val y: Int
}

data class Position(val x: Int, val y: Int)