fun main() {
    data class Food(val calories: Int)
    data class Elf(val inventory: List<Food>) {
        val calorieCount = inventory.sumOf { it.calories }
    }

    fun <T> List<T>.split(boundary: (T) -> Boolean): List<List<T>> {
        val original = this
        return buildList {
            var lastBoundary = 0
            original.forEachIndexed { index: Int, value: T ->
                val isLastIndex = index == original.lastIndex
                if (boundary(value) || isLastIndex) {
                    val toIndexExclusive = index.plus(if (isLastIndex) 1 else 0)
                    add(original.subList(lastBoundary, toIndexExclusive))
                    lastBoundary = index + 1
                }
            }
        }
    }

    fun List<String>.toElf() =
        Elf(map { Food(it.toInt()) })

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
