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
class Ship(private val stacks: List<Stack<Crate>>) {
    fun move(movement: Movement) {
        val source = stacks[movement.sourceId.asZeroBasedIndex()]
        val destination = stacks[movement.destinationId.asZeroBasedIndex()]
        repeat(movement.count) { destination.push(source.pop()) }
    }

    private fun Int.asZeroBasedIndex() = this - 1
    val message: String get() = stacks.map { it.peek().id }.joinToString("")

    companion object {
        fun parse(input: List<String>): Ship {
            val (crateInput, stackInput) = input.chunked(input.size - 1)

            return stackInput.single().mapIndexed { index, char -> index.takeIf { char != ' ' } }.filterNotNull()
                .map { index ->
                    Stack<Crate>().apply {
                        crateInput.reversed().forEach { line -> line.getOrNull(index)?.takeUnless { it == ' ' }?.let { add(Crate(it)) } }
                    }
                }.let(::Ship)
        }
    }
}

class Puzzle(private val ship: Ship, private val movements: List<Movement>) {
    fun solve(): String {
        movements.forEach(ship::move)
        return ship.message
    }

    companion object {
        fun parse(input: String): Puzzle {
            val (shipInput, movementInput) = input.split(nl.repeat(2)).map { it.split(nl) }
            return Puzzle(Ship.parse(shipInput), movementInput.filter { it.isNotEmpty() }.map { Movement.parse(it) })
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
