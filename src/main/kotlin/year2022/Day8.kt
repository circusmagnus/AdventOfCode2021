package year2022

fun day8(data: List<String>): Int {
    val grid = buildMap(data)
    initTrees(grid)
    val result = initVisibilityAndCount(grid)


    return result
}

private fun initVisibilityAndCount(grid: List<List<Tree>>) : Int {
    return grid.flatten().map { tree ->
        val (visible, score) = tree.isVisible()
        println("tree of $tree has score of: $score")
        score
    }.maxOf { scenicScore -> scenicScore }

//    var count: Int = 0
//    for (y in grid.indices) {
//        for (x in grid.first().indices) {
//            val tree = grid[y][x]
//            val score = tree.isVisible().second
//        }
//    }
//    return count
}

private data class Tree(val height: Int, val y: Int, val x: Int) {

    var left: Tree? = null
    var top: Tree? = null
    var right: Tree? = null
    var bottom: Tree? = null

    fun getNeighbours() = listOf(left, top, right, bottom)

//    private var visible: Boolean = false
//
    fun isVisible(): Pair<Boolean, Int> {
        println("getting visiblity of a Tree: $this")

    val (leftVisible, leftCount) = lookLeft(this.left, 0)
    val (topVisible, topCount) = lookUp(this.top, 0)
    val (rightVisible, rightCount) = lookRight(this.right, 0)
    val (bottomVisible, bottomCount) = lookDown(this.bottom, 0)

    return Pair(
        leftVisible || topVisible || rightVisible || bottomVisible,
        leftCount * topCount * rightCount * bottomCount
    )

    }

    private tailrec fun lookLeft(currentTree: Tree?, seenTrees: Int): Pair<Boolean, Int> = when {
            currentTree == null -> Pair(true, seenTrees)
            currentTree.height >= this.height -> Pair(false, seenTrees + 1)
            else -> lookLeft(currentTree.left, seenTrees + 1)
        }

    private tailrec fun lookUp(currentTree: Tree?, seenTrees: Int): Pair<Boolean, Int> = when {
        currentTree == null -> Pair(true, seenTrees)
        currentTree.height >= this.height -> Pair(false, seenTrees + 1)
        else -> lookUp(currentTree.top, seenTrees + 1)
    }

    private tailrec fun lookRight(currentTree: Tree?, seenTrees: Int): Pair<Boolean, Int> = when {
        currentTree == null -> Pair(true, seenTrees)
        currentTree.height >= this.height -> Pair(false, seenTrees + 1)
        else -> lookRight(currentTree.right, seenTrees + 1)
    }

    private tailrec fun lookDown(currentTree: Tree?, seenTrees: Int): Pair<Boolean, Int> = when {
        currentTree == null -> Pair(true, seenTrees)
        currentTree.height >= this.height -> Pair(false, seenTrees + 1)
        else -> lookDown(currentTree.bottom, seenTrees + 1)
    }
}

private fun initTrees(grid: List<List<Tree>>) {

    for (y in grid.indices) {
        for (x in grid.first().indices) {
            val tree = grid[y][x]
            tree.left = if (x > 0) grid[y][x - 1] else null
            tree.top = if (y > 0) grid[y - 1][x] else null
            tree.right = if (x < grid.first().lastIndex) grid[y][x + 1] else null
            tree.bottom = if (y < grid.lastIndex) grid[y + 1][x] else null


        }
    }
}

private fun getLeftNeighbour(tree: Tree) {

}

private fun buildMap(data: List<String>): List<List<Tree>> {
    return data.mapIndexed { yIndex, row -> treesFromRow(row, yIndex) }
}

private fun treesFromRow(input: String, yIndex: Int) =
    input.mapIndexed { xIndex, height -> Tree(height = height.digitToInt(), y = yIndex, x = xIndex) }