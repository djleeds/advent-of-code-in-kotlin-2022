fun main() {
    operator fun IntRange.contains(other: IntRange) = intersect(other).size == other.count()
    fun Pair<IntRange, IntRange>.hasFullOverlap() = first in second || second in first

    infix fun IntRange.containsAnyOf(other: IntRange) = intersect(other).isNotEmpty()
    fun Pair<IntRange, IntRange>.hasAnyOverlap() = first containsAnyOf second || second containsAnyOf first

    fun parseLine(line: String): Pair<IntRange, IntRange> =
        line
            .split(",")
            .map { it.split("-").map { id -> id.toInt() }.let { (start, end) -> IntRange(start, end) } }
            .let { (assignment1, assignment2) -> assignment1 to assignment2 }

    fun part1(input: List<String>): Int = input.map(::parseLine).count { it.hasFullOverlap() }
    fun part2(input: List<String>): Int = input.map(::parseLine).count { it.hasAnyOverlap() }

    val testInput = readInput("Day04_test")
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04")
    println(part1(input))
    println(part2(input))
}
