package year2022

fun day11(data: List<String>): ULong {
    val monkeys = getMonkeys()
    val commonMagicNumber = monkeys.map { monkey -> monkey.test.divisor }.reduce { acc, divisor -> acc * divisor }

    val result = runGame(0, monkeys, commonMagicNumber)
    return result
}

private tailrec fun runGame(index: Int, monkeys: List<Monkey>, magicNumber: UInt): ULong {
    val prod = 10_000
    val test = 20
    return if (index == prod) {
        countMonkeyBusiness(monkeys)
    } else {
        runRound(monkeys, monkeys, magicNumber)

        runGame(index + 1, monkeys, magicNumber)
    }
}

private fun countMonkeyBusiness(monkeys: List<Monkey>): ULong {
    println("Monkeys when counting business: $monkeys")
    val (first, second) = monkeys.sortedByDescending { monkey -> monkey.inspectionCount }.take(2)
    return first.inspectionCount * second.inspectionCount
}

private tailrec fun runRound(monkeys: List<Monkey>, monkeysLeft: List<Monkey>, magicNumber: UInt) {
    return if (monkeysLeft.isEmpty()) {
        Unit
    } else {
        val monkey = monkeysLeft.first()
        monkey.throwItems(monkeys, magicNumber)
        runRound(monkeys, monkeysLeft.drop(1), magicNumber)
    }
}

private data class Test(val divisor: UInt, val ifTrue: Int, val ifFalse: Int) {

    fun apply(input: ULong): Int {
        return if (input % divisor == 0uL) ifTrue else ifFalse
    }
}

enum class Op { MULT, ADD, SQUARE }

private data class Operation(val value: UInt, val op: Op) {
    fun apply(input: ULong): ULong {
        return when (op) {
            Op.MULT -> input * value
            Op.ADD -> input + value
            Op.SQUARE -> input * input
        }
    }
}

private class Monkey(
    val name: Int,
    val items: ArrayDeque<ULong>,
    val operation: Operation,
    val test: Test
) {

    var inspectionCount: ULong = 0u

    fun catch(item: ULong) {
        items.addLast(item)
    }

    tailrec fun throwItems(other: List<Monkey>, magicNumber: UInt) {
        val item = items.removeFirstOrNull() ?: return
//        println("$this is inspecting $item")
        inspectionCount++

//        val whomToThrow = when(operation.op) {
//            Op.MULT -> if(operation.value % test.divisor == 0 || item % test.divisor == 0) test.ifTrue else test.ifFalse
//            Op.SQUARE -> if(item % test.divisor == 0) test.ifTrue else test.ifFalse
//            Op.ADD -> if((item + operation.value) % test.divisor == 0) test.ifTrue else test.ifFalse
//        }
//
//        val newWorryLevel = when(operation.op) {
//            Op.MULT -> item
//            Op.ADD -> item + operation.value
//            Op.SQUARE -> item
//        }


        val worryLevelAfterInspection = operation.apply(item)


        val afterRelief = worryLevelAfterInspection % magicNumber
        val whomToThrowTo = test.apply(afterRelief)
//            if(worryLevelAfterInspection % test.divisor == 0uL) worryLevelAfterInspection / test.divisor else worryLevelAfterInspection

//        println("$this throwing $worryLevelAfterInspection to: $whomToThrowTo")

        other.first { it.name == whomToThrowTo }.catch(afterRelief)
        throwItems(other, magicNumber)
    }

    override fun toString(): String {
        return "Monkey $name, inspectionCOunt: ${inspectionCount}, items: $items"
    }
}

private fun getTestMonkeys() = listOf(
    Monkey(
        0,
        ArrayDeque<ULong>().apply { add(79u); add(98u); },
        Operation(19u, Op.MULT),
//        operation = { old -> old * 11 },
        test = Test(23u, 2, 3)
    ),
    Monkey(
        1,
        ArrayDeque<ULong>().apply { add(54u); add(65u); add(75u); add(74u); },
        Operation(6u, Op.ADD),
//        operation = { old -> old * 11 },
        test = Test(19u, 2, 0)
    ),
    Monkey(
        2,
        ArrayDeque<ULong>().apply { add(79u); add(60u); add(97u); },
        Operation(0u, Op.SQUARE),
//        operation = { old -> old * 11 },
        test = Test(13u, 1, 3)
    ),
    Monkey(
        3,
        ArrayDeque<ULong>().apply { add(74u); },
        Operation(3u, Op.ADD),
//        operation = { old -> old * 11 },
        test = Test(17u, 0, 1)
    ),
)


private fun getMonkeys() = listOf(
    Monkey(
        0,
        ArrayDeque<ULong>().apply { add(89u); add(95u); add(92u); add(64u); add(87u); add(68u) },
        Operation(11u, Op.MULT),
//        operation = { old -> old * 11 },
        test = Test(2u, 7, 4)
    ),
    Monkey(
        1,
        ArrayDeque<ULong>().apply { add(87u); add(67u) },
        Operation(1u, Op.ADD,),
//        operation = { old -> old + 1 },
        Test(13u, 3, 6)
//        test = Test(13, 3, 6)
    ),
    Monkey(
        2,
        ArrayDeque<ULong>().apply { add(95u); add(79u); add(92u); add(82u); add(60u); },
        Operation(6u, Op.ADD),
//        operation = { old -> old + 6 },
        Test(3u, 1, 6)
//        test = { worry -> if (worry % 3 == 0) 1 else 6 }
    ),
    Monkey(
        3,
        ArrayDeque<ULong>().apply { add(67u); add(97u); add(56u); },
        Operation(0u, Op.SQUARE),
//        operation = { old -> old * old },
        Test(17u, 7, 0)
//        test = { worry -> if (worry % 17 == 0) 7 else 0 }
    ),
    Monkey(
        4,
        ArrayDeque<ULong>().apply { add(80u); add(68u); add(87u); add(94u); add(61u); add(59u); add(50u); add(68u) },
        Operation(7u, Op.MULT),
//        operation = { old -> old * 7 },
        Test(19u, 5, 2)
//        test = { worry -> if (worry % 19 == 0) 5 else 2 }
    ),
    Monkey(
        5,
        ArrayDeque<ULong>().apply { add(73u); add(51u); add(76u); add(59u); },
        Operation(8u, Op.ADD),
//        operation = { old -> old + 8 },
        Test(7u, 2, 1)
//        test = { worry -> if (worry % 7 == 0) 2 else 1 }
    ),
    Monkey(
        6,
        ArrayDeque<ULong>().apply { add(92u); },
        Operation(5u, Op.ADD),
//        operation = { old -> old + 5 },
        Test(11u, 3, 0)
//        test = { worry -> if (worry % 11 == 0) 3 else 0 }
    ),
    Monkey(
        7,
        ArrayDeque<ULong>().apply { add(99u); add(76u); add(78u); add(76u); add(79u); add(90u); add(89u) },
        Operation(7u, Op.ADD),
//        operation = { old -> old + 7 },
        Test(5u, 4, 5)
//        test = { worry -> if (worry % 5 == 0) 4 else 5 }
    ),
)