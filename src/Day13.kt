import kotlinx.serialization.json.*
import java.util.*
import kotlin.math.max

fun <T, R> List<T>.zipWithPadding(other: List<R>): List<Pair<T?, R?>> =
    buildList { for (i in 0 until max(this@zipWithPadding.size, other.size)) add(this@zipWithPadding.getOrNull(i) to other.getOrNull(i)) }

private fun JsonArray.zipWithPadding(other: JsonArray) = this.toList().zipWithPadding(other.toList())

private fun isInRightOrder(leftPacket: JsonArray, rightPacket: JsonArray) = leftPacket < rightPacket

operator fun JsonArray.compareTo(other: JsonArray): Int {
    val stack = Stack<Pair<JsonElement?, JsonElement?>>().apply { addAll(this@compareTo.zipWithPadding(other).reversed()) }

    while (stack.isNotEmpty()) {
        val (left, right) = stack.pop()

        when {
            left == null && right != null                   -> return -1
            left != null && right == null                   -> return 1
            left is JsonPrimitive && right is JsonPrimitive -> when {
                left.int < right.int  -> return -1
                left.int == right.int -> continue
                left.int > right.int  -> return 1
            }

            left is JsonArray && right is JsonArray         -> left.zipWithPadding(right).reversed().forEach(stack::add)
            left is JsonPrimitive && right is JsonArray     -> stack.push(JsonArray(listOf(left)) to right)
            left is JsonArray && right is JsonPrimitive     -> stack.push(left to JsonArray(listOf(right)))
            else                                            -> throw IllegalArgumentException()
        }
    }

    throw IllegalStateException()
}

fun dividerPacket(number: Int): JsonArray = JsonArray(listOf(JsonArray(listOf(JsonPrimitive(number)))))

fun main() {
    fun parse(input: List<String>) = input.filter { it.isNotEmpty() }.map { Json.parseToJsonElement(it).jsonArray }

    fun part1(input: List<String>): Int = parse(input)
        .chunked(2)
        .map { isInRightOrder(it[0], it[1]) }
        .mapIndexed { index, result -> (index + 1).takeIf { result } ?: 0 }
        .sumOf { it }

    fun part2(input: List<String>): Int {
        val divider2 = dividerPacket(2)
        val divider6 = dividerPacket(6)
        return buildList { addAll(parse(input)); add(divider2); add(divider6) }
            .sortedWith(JsonArray::compareTo)
            .mapIndexed { index, it -> if (it == divider2 || it == divider6) index + 1 else 1 }
            .fold(1) { acc, it -> acc * it }
    }

    val testInput = readInput("Day13_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day13")
    println(part1(input))
    println(part2(input))
}
