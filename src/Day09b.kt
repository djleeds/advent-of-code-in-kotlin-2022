import kotlin.math.abs
import kotlin.math.sign

data class Motion(val direction: String, val count: Int)
data class Position(val x: Int, val y: Int)

private fun parse(input: List<String>) = input.map { it.split(" ").let { m -> Motion(m[0], m[1].toInt()) } }

fun main() {
    part1()
    part2()
}

fun part1() {
    val motions = parse(readInput("Day09_test"))

    var head = Position(0, 0)
    var tail = Position(0, 0)

    val tailVisits = mutableSetOf(tail)

    motions.forEach { motion ->
        repeat(motion.count) {
            head = applyMotion(head, motion)
            tail = tug(head, tail)
            tailVisits.add(tail)
        }
    }

    println(tailVisits.size)
}


fun part2() {
    val motions = parse(listOf("R 5", "U 8", "L 8", "D 3", "R 17", "D 10", "L 25", "U 20"))

    val rope = Array(10) { Position(0, 0) }
    val tailVisits = mutableSetOf(rope.last())

    motions.forEach { motion ->
        repeat(motion.count) {
            rope[0] = applyMotion(rope[0], motion)

            for (i in 0..(rope.size - 2)) {
                rope[i + 1] = tug(rope[i], rope[i + 1])
            }

            tailVisits.add(rope.last())
        }
    }

    println(tailVisits.size)

}

private fun applyMotion(head: Position, motion: Motion) = Position(
    x = head.x + when (motion.direction) {
        "L" -> -1; "R" -> 1; else -> 0
    },
    y = head.y + when (motion.direction) {
        "U" -> 1; "D" -> -1; else -> 0
    }
)

fun tug(head: Position, tail: Position): Position {
    val areTouching = abs(head.x - tail.x) <= 1 && abs(head.y - tail.y) <= 1
    val areInSameRow = head.y == tail.y
    val areInSameCol = head.x == tail.x

    return when {
        areInSameRow && head.x - tail.x == 2 -> Position(tail.x + 1, tail.y)
        areInSameRow && tail.x - head.x == 2 -> Position(tail.x - 1, tail.y)
        areInSameCol && head.y - tail.y == 2 -> Position(tail.x, tail.y + 1)
        areInSameCol && tail.y - head.y == 2 -> Position(tail.x, tail.y - 1)
        else                                 -> {
            if (!areTouching && !areInSameRow && !areInSameCol) {
                val moveX = (head.x - tail.x).sign
                val moveY = (head.y - tail.y).sign
                Position(tail.x + moveX, tail.y + moveY)
            } else {
                tail
            }
        }
    }
}
