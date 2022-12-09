import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sign
import kotlin.properties.Delegates.observable

data class Vector2(val x: Int = 0, val y: Int = 0) {
    fun isAdjacentTo(other: Vector2) = max(abs(x - other.x), abs(y - other.y)) <= 1
    operator fun plus(other: Vector2) = Vector2(x + other.x, y + other.y)
    operator fun minus(other: Vector2) = Vector2(x - other.x, y - other.y)
    fun normalizedComponents() = Vector2(x.sign, y.sign)

    companion object {
        val ORIGIN = Vector2(0, 0)
        val UP = Vector2(0, 1)
        val DOWN = Vector2(0, -1)
        val LEFT = Vector2(-1, 0)
        val RIGHT = Vector2(1, 0)
    }
}

class Knot(val next: Knot? = null) {
    val visited: MutableSet<Vector2> = mutableSetOf(Vector2.ORIGIN)

    private var position: Vector2 by observable(Vector2.ORIGIN) { _, _, new ->
        if (next?.position?.isAdjacentTo(new) == false) next.position += (new - next.position).normalizedComponents()
        visited.add(new)
    }

    fun tail(): Knot = next?.tail() ?: this

    fun move(instructions: List<String>) =
        instructions.map { it.split(" ") }.forEach { (dir, count) -> move(dir, count.toInt()) }

    private fun move(direction: String, count: Int) = repeat(count) {
        position += when (direction) {
            "L"  -> Vector2.LEFT
            "R"  -> Vector2.RIGHT
            "U"  -> Vector2.UP
            "D"  -> Vector2.DOWN
            else -> throw IllegalArgumentException("Unexpected movement direction: $direction")
        }
    }

    companion object {
        fun chain(knotCount: Int): Knot = (1 until knotCount).fold(Knot()) { acc, _ -> Knot(acc) }
    }
}

fun main() {
    fun part1(input: List<String>) = Knot.chain(2).apply { move(input) }.tail().visited.size
    fun part2(input: List<String>) = Knot.chain(10).apply { move(input) }.tail().visited.size


    val testInput = readInput("Day09_test")
    println(part1(testInput))
    println(part2(listOf("R 5", "U 8", "L 8", "D 3", "R 17", "D 10", "L 25", "U 20")))

    val input = readInput("Day09")
    println(part1(input))
    println(part2(input))
}
