fun <T> List<T>.split(boundary: (T) -> Boolean): List<List<T>> =
    mapIndexed { index, value -> index.takeIf { boundary(value) } }
        .filterNotNull()
        .let { listOf(-1) + it + size }
        .windowed(2) { (first, second) -> subList(first + 1, second) }

operator fun <T> List<T>.component6() = this[5]
operator fun <T> List<T>.component7() = this[6]
operator fun <T> List<T>.component8() = this[7]

@JvmInline
value class Item(val worryLevel: Long)

sealed class Operand(val description: String) {
    data class Num(val value: Long) : Operand(value.toString())
    object Old : Operand("itself")

    companion object {
        fun parse(string: String) = when (string) {
            "old" -> Old
            else  -> Num(string.toLong())
        }
    }
}

enum class Op(val symbol: String, val description: String, val execute: (Long, Long) -> Long) {
    MULTIPLY("*", "is multiplied by", { l, r -> l * r }),
    DIVIDE("/", "is divided by", { l, r -> l / r }),
    ADD("+", "increases by", { l, r -> l + r }),
    SUBTRACT("-", "decreases by", { l, r -> l - r });

    companion object {
        fun parse(string: String) = values().first { it.symbol == string }
    }
}

data class Operation(val left: Operand, val op: Op, val right: Operand) {
    fun execute(item: Item): Item =
        Item(op.execute(operandValue(left, item), operandValue(right, item)))

    private fun operandValue(operand: Operand, item: Item) = when (operand) {
        is Operand.Num -> operand.value
        else           -> item.worryLevel
    }

    companion object {
        fun parse(lh: String, op: String, rh: String) = Operation(Operand.parse(lh), Op.parse(op), Operand.parse(rh))
    }
}

data class Test(val divisibleBy: Long)

interface WorryMitigation {
    fun mitigate(item: Item): Item
}

object DivideBy3 : WorryMitigation {
    override fun mitigate(item: Item) =
        Item(item.worryLevel / 3)
            .also { log { "    Monkey gets bored with item. Worry level is divided by 3 to ${it.worryLevel}." } }
}

class ManageOverflows(private val amount: Long) : WorryMitigation {
    override fun mitigate(item: Item) =
        Item(item.worryLevel.rem(amount))
            .also { log { "    Mitigating worry with math super powers to ${it.worryLevel}" } }
}

class Monkey(
    val id: Int,
    val inventory: MutableList<Item>,
    private val operation: Operation,
    val test: Test,
    private val nextMonkeyIdWhenTrue: Int,
    private val nextMonkeyIdWhenFalse: Int
) {
    var inspectionCount: Long = 0L; private set

    fun inspectItems(worryMitigation: WorryMitigation, throwItem: (Pair<Item, Int>) -> Unit) {
        log { "Monkey $id:" }
        inventory.map { inspectItem(it, worryMitigation) }.map(throwItem)
        inventory.clear()
    }

    private fun inspectItem(item: Item, worryMitigation: WorryMitigation): Pair<Item, Int> {
        inspectionCount++
        log { "  Monkey inspects an item with a worry level of ${item.worryLevel}." }
        val itemAfterOperation = operation.execute(item)
        log { "    Worry level ${operation.op.description} ${operation.right.description} to ${itemAfterOperation.worryLevel}." }
        val itemAfterWorryMitigation = worryMitigation.mitigate(itemAfterOperation)
        val isDivisible = itemAfterWorryMitigation.worryLevel.mod(test.divisibleBy) == 0L
        log { "    Current worry level is${if (isDivisible) "" else " not"} divisible by ${test.divisibleBy}." }
        val nextMonkeyId = if (isDivisible) nextMonkeyIdWhenTrue else nextMonkeyIdWhenFalse
        log { "    Item with worry level ${itemAfterWorryMitigation.worryLevel} is thrown to monkey $nextMonkeyId." }

        return itemAfterWorryMitigation to nextMonkeyId
    }

    fun receiveItem(item: Item) {
        inventory.add(item)
    }

    companion object {
        private val pattern = Regex(
            """
            Monkey (\d+):
            \s+Starting items: (.*)
            \s+Operation: new = (old|\d+) ([\*\+\/\-]) (old|\d+)
            \s+Test: divisible by (\d+)
            \s+If true: throw to monkey (\d+)
            \s+If false: throw to monkey (\d+)
        """.trimIndent().trim(), RegexOption.MULTILINE
        )

        fun parse(input: List<String>) =
            pattern.find(input.joinToString("\n"))
                ?.let { result ->
                    val (id, items, lh, op, rh, test, trueMonkeyId, falseMonkeyId) = result.groupValues.drop(1)
                    Monkey(
                        id.toInt(),
                        items.split(", ").map { Item(it.toLong()) }.toMutableList(),
                        Operation.parse(lh, op, rh),
                        Test(test.toLong()),
                        trueMonkeyId.toInt(),
                        falseMonkeyId.toInt()
                    )
                }
                ?: throw IllegalArgumentException()
    }
}

fun main() {
    fun solve(monkeys: List<Monkey>, mitigation: WorryMitigation, rounds: Int): Long {
        for (round in 1..rounds) {
            for (monkey in monkeys) {
                monkey.inspectItems(mitigation) { (item, monkeyId) -> monkeys[monkeyId].receiveItem(item) }
            }
            log { "After round $round, the monkeys are holding items with these worry levels:" }
            monkeys.forEach { monkey -> log { "Monkey ${monkey.id}: ${monkey.inventory.map { it.worryLevel }.joinToString(", ")}" } }
            log { "------------------------------------------------------------------------" }
        }

        for (monkey in monkeys) {
            log { "Monkey ${monkey.id} inspected items ${monkey.inspectionCount} times." }
        }

        val monkeyBusiness = monkeys.map { it.inspectionCount }.sortedDescending().take(2).let { (first, second) -> first * second }
        log { "Total amount of monkey business: $monkeyBusiness" }

        return monkeyBusiness
    }

    fun part1(input: List<String>): Long {
        val monkeys = input.split { it.isEmpty() }.map(Monkey::parse)
        val mitigation = DivideBy3
        return solve(monkeys, mitigation, 20)
    }

    fun part2(input: List<String>): Long {
        val monkeys = input.split { it.isEmpty() }.map(Monkey::parse)
        val mitigation = ManageOverflows(monkeys.map { it.test.divisibleBy }.reduce(Long::times))
        return solve(monkeys, mitigation, 10_000)
    }

    val testInput = readInput("Day11_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day11")
    println(part1(input))
    println(part2(input))
}
