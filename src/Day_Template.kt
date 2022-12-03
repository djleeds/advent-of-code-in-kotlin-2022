fun main() {
    fun part1(input: List<String>): Int = 0

    fun part2(input: List<String>): Int = 0

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24_000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
