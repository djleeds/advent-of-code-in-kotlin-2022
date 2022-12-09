import kotlin.math.abs
import kotlin.math.sign

data class Motion(val direction: Dir, val count: Int)
enum class Dir(private val code: String, val vector: Vec2) {
    UP("U", Vec2(0, 1)), DOWN("D", Vec2(0, -1)), LEFT("L", Vec2(-1, 0)), RIGHT("R", Vec2(1, 0));

    companion object {
        fun fromCode(code: String): Dir = values().first { it.code == code }
    }
}

data class Vec2(val x: Int, val y: Int) {
    fun isTouching(other: Vec2) = abs(x - other.x) <= 1 && abs(y - other.y) <= 1
    fun isSameRowAs(other: Vec2) = y == other.y
    fun isSameColAs(other: Vec2) = x == other.x
    operator fun minus(other: Vec2) = Vec2(x - other.x, y - other.y)
    operator fun plus(other: Vec2) = Vec2(x + other.x, y + other.y)
}

private fun parse(input: List<String>) =
    input.map { it.split(" ").let { m -> Motion(Dir.fromCode(m[0]), m[1].toInt()) } }

fun main() {
    part1()
    part2()
}

fun part1() {
    val motions = parse(readInput("Day09_test"))
    println(solve(motions, 2))
}


fun part2() {
    val motions = parse(listOf("R 5", "U 8", "L 8", "D 3", "R 17", "D 10", "L 25", "U 20"))
    println(solve(motions, 10))
}

private fun solve(motions: List<Motion>, ropeSize: Int): Int {
    val rope = Array(ropeSize) { Vec2(0, 0) }
    val tailVisits = mutableSetOf(rope.last())

    motions.forEach { motion ->
        repeat(motion.count) {
            rope[0] = rope[0] + motion.direction.vector

            for (i in 0..(rope.size - 2)) {
                rope[i + 1] = tug(rope[i], rope[i + 1])
            }

            tailVisits.add(rope.last())
        }
    }
    return tailVisits.size
}

fun tug(head: Vec2, tail: Vec2): Vec2 {
    val areTouching = head.isTouching(tail)
    val areInSameRow = head.isSameRowAs(tail)
    val areInSameCol = head.isSameColAs(tail)

    return when (head - tail) {
        Vec2(2, 0)   -> Vec2(tail.x + 1, tail.y)
        Vec2(-2, -0) -> Vec2(tail.x - 1, tail.y)
        Vec2(0, 2)   -> Vec2(tail.x, tail.y + 1)
        Vec2(0, -2)  -> Vec2(tail.x, tail.y - 1)
        else         -> {
            if (!areTouching && !areInSameRow && !areInSameCol) {
                Vec2(tail.x + (head.x - tail.x).sign, tail.y + (head.y - tail.y).sign)
            } else {
                tail
            }
        }
    }
}
