import kotlin.math.abs
import kotlin.math.sign

data class Motion(val direction: String, val count: Int)
data class Position(val x: Int, val y: Int)

private fun parse(input: List<String>) = input.map { it.split(" ").let { m -> Motion(m[0], m[1].toInt()) } }

fun main() {
    val motions = parse(readInput("Day09_test"))

    var head = Position(0, 0)
    var tail = Position(0, 0)

    val tailVisits = mutableSetOf(tail)

    motions.forEach { motion ->
        println(motion)
        repeat(motion.count) {
            head = Position(
                x = head.x + when (motion.direction) {
                    "L" -> -1; "R" -> 1; else -> 0
                },
                y = head.y + when (motion.direction) {
                    "U" -> 1; "D" -> -1; else -> 0
                }
            )

            val areTouching = abs(head.x - tail.x) <= 1 && abs(head.y - tail.y) <= 1
            val areInSameRow = head.y == tail.y
            val areInSameCol = head.x == tail.x

            when {
                areInSameRow && head.x - tail.x == 2 -> tail = Position(tail.x + 1, tail.y)
                areInSameRow && tail.x - head.x == 2 -> tail = Position(tail.x - 1, tail.y)
                areInSameCol && head.y - tail.y == 2 -> tail = Position(tail.x, tail.y + 1)
                areInSameCol && tail.y - head.y == 2 -> tail = Position(tail.x, tail.y - 1)
                else                                 -> {
                    if (!areTouching && !areInSameRow && !areInSameCol) {
                        val moveX = (head.x - tail.x).sign
                        val moveY = (head.y - tail.y).sign
                        tail = Position(tail.x + moveX, tail.y + moveY)
                    }
                }
            }

            println(tail)
            tailVisits.add(tail)
        }
    }

    println(tailVisits.size)

}
