import java.util.*

typealias Stacks = List<Stack<Crate>>
typealias CrateMover = (src: Stack<Crate>, dst: Stack<Crate>, count: Int) -> Unit

private object Parser {
    private const val FIELD_SIZE = 4
    private val newline = System.lineSeparator()
    private val movementRegex = Regex("move (\\d+) from (\\d+) to (\\d+)")

    fun parse(input: String): Pair<() -> Stacks, List<Movement>> {
        val (crane, movement) = input.split(newline.repeat(2)).map { it.split(newline) }
        return { parseStacks(crane) } to movement.filter { it.isNotEmpty() }.map(::parseMovement)
    }

    private fun parseMovement(input: String) =
        movementRegex.find(input)?.groupValues?.let { Movement(it[1].toInt(), it[2].toInt(), it[3].toInt()) } ?: throw IllegalArgumentException()

    private fun parseStacks(input: List<String>): List<Stack<Crate>> {
        val lines = input.reversed().drop(1)

        return (1..input.last().length step FIELD_SIZE).map { index ->
            lines.fold(Stack<Crate>()) { stack, item ->
                stack.apply { item.getOrNull(index)?.takeUnless { it == ' ' }?.let { push(Crate(it)) } }
            }
        }
    }
}

data class Movement(val count: Int, val sourceId: Int, val destinationId: Int)

data class Crate(val id: Char)

class Crane(private val stacks: Stacks, private val move: CrateMover) {
    val message: String get() = stacks.map { it.peek().id }.joinToString("")

    fun move(movements: List<Movement>) = movements.forEach(::move)
    private fun move(movement: Movement) = with(movement) { move(stacks[sourceId - 1], stacks[destinationId - 1], count) }
}

val crateMover9000: CrateMover = { source, destination, count -> repeat(count) { destination.push(source.pop()) } }
val crateMover9001: CrateMover = { source, destination, count -> (0 until count).map { source.pop() }.reversed().forEach(destination::push) }

fun main() {
    fun part1(stacks: Stacks, movements: List<Movement>): String = Crane(stacks, crateMover9000).apply { move(movements) }.message
    fun part2(stacks: Stacks, movements: List<Movement>): String = Crane(stacks, crateMover9001).apply { move(movements) }.message

    Parser.parse(readInputAsText("Day05_test")).let { (stacks, movements) ->
        check(part1(stacks(), movements) == "CMZ")
        check(part2(stacks(), movements) == "MCD")
    }

    Parser.parse(readInputAsText("Day05")).let { (stacks, movements) ->
        println(part1(stacks(), movements))
        println(part2(stacks(), movements))
    }
}
