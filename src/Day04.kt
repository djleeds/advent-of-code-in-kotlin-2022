private class Assignment(start: Int, end: Int) {
    val range = start..end

    infix fun containsAllOf(other: Assignment) = range.intersect(other.range).size == other.range.count()
    infix fun containsAnyOf(other: Assignment) = range.intersect(other.range).isNotEmpty()

    companion object {
        fun parse(input: String) = input.split("-").map { id -> id.toInt() }.let { (start, end) -> Assignment(start, end) }
    }
}

private class AssignmentPair(val first: Assignment, val second: Assignment) {
    fun hasFullOverlap() = first containsAllOf second || second containsAllOf first
    fun hasAnyOverlap() = first containsAnyOf second || second containsAnyOf first

    companion object {
        fun parse(input: String) = input.split(",").map(Assignment::parse).let { (first, second) -> AssignmentPair(first, second) }
    }
}

fun main() {
    fun part1(input: List<AssignmentPair>): Int = input.count { it.hasFullOverlap() }
    fun part2(input: List<AssignmentPair>): Int = input.count { it.hasAnyOverlap() }

    val testInput = readInput("Day04_test").map(AssignmentPair::parse)
    check(part1(testInput) == 2)
    check(part2(testInput) == 4)

    val input = readInput("Day04").map(AssignmentPair::parse)
    println(part1(input))
    println(part2(input))
}
