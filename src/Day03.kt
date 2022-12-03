fun main() {
    fun Char.toPriority() = if (code <= 'Z'.code) this - 'A' + 27 else this - 'a' + 1

    fun part1(input: List<String>): Int =
        input
            .map { rucksack -> rucksack.chunked(rucksack.length / 2) }
            .map { (compartment1, compartment2) -> compartment1.first { it in compartment2 } }
            .sumOf { it.toPriority() }

    fun part2(input: List<String>): Int =
        input
            .chunked(3)
            .map { (sack1, sack2, sack3) -> sack1.first { it in sack2 && it in sack3 } }
            .sumOf { it.toPriority() }

    val testInput = readInput("Day03_test")
    check(part1(testInput) == 157)
    check(part2(testInput) == 70)

    val input = readInput("Day03")
    println(part1(input))
    println(part2(input))
}
