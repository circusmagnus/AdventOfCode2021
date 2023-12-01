package year2021

fun day4(input: List<String>): Int {
    println(input)
    val numbers = input.first().split(",").map { it.toInt() }.map { BingoNumber(value = it) }
    println("numbers: $numbers")
    val boards = getBoards(input.drop(1))
    val winningScore = markNumbers(numbers, boards)
    return winningScore
}

private fun markNumbers(numbers: List<BingoNumber>, boards: List<Board>): Int {
    val boardsThatWon = mutableSetOf<Board>()
//    val boardsInCompetition = mutableSetOf<Board>()
    for (currentNumber in numbers) {
        println("current number is: $currentNumber")
        for (board in boards) {
            board.markNumber(currentNumber)
            if(board.isWinning) {
                boardsThatWon.add(board)
//                boardsInCompetition
                println("winning board is $board. Winning boards size is: ${boardsThatWon.size}")
                if(boardsThatWon.size == boards.size) return board.getWinningScore(currentNumber.value)
            }
        }
    }
    throw IllegalStateException()
}

data class BingoNumber(var isSelected: Boolean = false, val value: Int)

class Board(val content: List<List<BingoNumber>>) {

    private val columnedContent = buildColumns(content, emptyList())

    val isWinning
        get() = hasMarkedRow() || hasMarkedColumn()

    private fun hasMarkedRow() = content.any { it.all { number -> number.isSelected } }
    private fun hasMarkedColumn() = columnedContent.any { it.all { number -> number.isSelected } }

    @OptIn(ExperimentalStdlibApi::class)
    private tailrec fun buildColumns(
        tempContent: List<List<BingoNumber>>,
        columnedContent: List<List<BingoNumber>>
    ): List<List<BingoNumber>> {
        return if (tempContent.first().isEmpty()) columnedContent
        else {
            val atIndex0 = tempContent.map { it.first() }
            val newColumned = buildList {
                addAll(columnedContent)
                add(atIndex0)
            }
            val newTemp = tempContent.map { it.drop(1) }
            buildColumns(newTemp, newColumned)
        }

    }

    fun markNumber(number: BingoNumber) {
        for (row in content) {
            for (existingNumber in row) {
                if (number.value == existingNumber.value) existingNumber.isSelected = true
            }
        }
    }

    fun getWinningScore(markedNumber: Int) : Int {
        val unselected = content
            .flatten()
            .sumOf { bingoNumber -> if (bingoNumber.isSelected) 0 else bingoNumber.value }
        println("unselected sum of $this = $unselected")

        return unselected * markedNumber
    }
}

@OptIn(ExperimentalStdlibApi::class)
private fun getBoards(rawData: List<String>): List<Board> {
    val sanitized = rawData.filterNot { it == "" }

    tailrec fun getBoards(toRead: List<String>, boards: List<Board>): List<Board> {
        return if (toRead.isEmpty()) boards
        else {
            val nextRawBoard = toRead.take(5)
            val newBoard = getBoardWithStrings(nextRawBoard)
            val accumulatedBoards = buildList {
                addAll(boards)
                add(newBoard)
            }
            getBoards(toRead.drop(5), accumulatedBoards)
        }
    }

    val boards = getBoards(sanitized, emptyList())
    check(boards.all { it.content.size == 5 && it.content.all { it.size == 5 } })

//    for (board in boards) {
//        for (row in board.content) {
//            println("boardRow: $row")
//        }
//        println(" ")
//    }
    return boards
}

private fun getBoardWithStrings(data: List<String>): Board = Board(data.take(5).map { it.toNumbers() })

private fun String.toNumbers(): List<BingoNumber> =
    split(" ").filterNot { it == "" }.map { it.toInt() }.map { BingoNumber(value = it) }