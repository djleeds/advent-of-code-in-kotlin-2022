import java.util.*

private val nl = System.lineSeparator()

class Movement(val count: Int, val sourceId: Int, val destinationId: Int) {
    companion object {
        private val pattern = Regex("move (\\d+) from (\\d+) to (\\d+)")
        fun parse(input: String) =
            pattern.find(input)?.groupValues?.let { Movement(it[1].toInt(), it[2].toInt(), it[3].toInt()) } ?: throw IllegalArgumentException()
    }
}

class Crate(val id: Char)

class Crane(private val stacks: List<Stack<Crate>>) {
    fun move(movement: Movement) {
        val source = stacks[movement.sourceId.asZeroBasedIndex()]
        val destination = stacks[movement.destinationId.asZeroBasedIndex()]
        repeat(movement.count) { destination.push(source.pop()) }
    }

    private fun Int.asZeroBasedIndex() = this - 1
    val message: String get() = stacks.map { it.peek().id }.joinToString("")

    companion object {
        private const val fieldSize = 4
        fun parse(input: List<String>): Crane {
            val lines = input.reversed().drop(1)

            return (1..input.last().length step fieldSize).map { index ->
                lines.fold(Stack<Crate>()) { stack, item ->
                    stack.apply { item.getOrNull(index)?.takeUnless { it == ' ' }?.let { push(Crate(it)) } }
                }
            }.let(::Crane)
        }
    }
}

class Puzzle(private val crane: Crane, private val movements: List<Movement>) {
    fun solve(): String {
        movements.forEach(crane::move)
        return crane.message
    }

    companion object {
        fun parse(input: String): Puzzle {
            val (crane, movement) = input.split(nl.repeat(2)).map { it.split(nl) }
            return Puzzle(Crane.parse(crane), movement.filter { it.isNotEmpty() }.map(Movement::parse))
        }
    }
}

fun main() {
    fun part1(input: String): String = Puzzle.parse(input).solve()

    fun part2(input: String): String = ""

    val testInput = readInputAsText("Day05_test")
    check(part1(testInput) == "CMZ")

    val input = readInputAsText("Day05")
    println(part1(input))
//    println(part2(input))
}
