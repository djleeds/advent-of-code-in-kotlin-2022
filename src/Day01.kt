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
                if (boundary(value)) {
                    add(original.subList(lastBoundary, index))
                    lastBoundary = index + 1
                }
            }
        }
    }

    fun elves(input: List<String>) = input.split { it.isEmpty() }.map { data -> Elf(data.map { it.toInt() }.map(::Food)) }

    fun part1(input: List<String>): Int =
        elves(input).maxBy { it.calorieCount }.calorieCount

    fun part2(input: List<String>): Int =
        elves(input).sortedByDescending { it.calorieCount }.take(3).sumOf { it.calorieCount }

    val input = readInput("Day01")
    println(part1(input))
    println(part2(input))
}
