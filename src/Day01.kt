fun main() {
    data class Food(val calories: Int)
    data class Elf(val inventory: List<Food>) {
        val calorieCount = inventory.sumOf { it.calories }
    }

    fun List<String>.toElf() = Elf(map { Food(it.toInt()) })

    fun <T> List<T>.split(boundary: (T) -> Boolean): List<List<T>> =
        mapIndexed { index, value -> index.takeIf { boundary(value) } }
            .filterNotNull()
            .let { listOf(-1) + it + size }
            .windowed(2) { (first, second) -> subList(first + 1, second) }

    fun parse(input: List<String>) =
        input
            .split { it.isEmpty() }
            .map { it.toElf() }

    fun part1(input: List<String>): Int =
        parse(input)
            .maxBy { it.calorieCount }
            .calorieCount

    fun part2(input: List<String>): Int =
        parse(input)
            .sortedByDescending { it.calorieCount }
            .take(3)
            .sumOf { it.calorieCount }

    val testInput = readInput("Day01_test")
    check(part1(testInput) == 24_000)

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
