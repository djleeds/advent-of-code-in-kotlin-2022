fun main() {
    fun part1(instructions: List<String>): Int {
        val registerValues = mutableListOf(1) // represents the value AFTER the tick of the index
        instructions.forEach { instruction ->
            when {
                instruction == "noop"          -> registerValues.add(registerValues.last())
                instruction.startsWith("addx") -> {
                    val amount = instruction.substringAfter(" ").toInt()
                    registerValues.add(registerValues.last())
                    registerValues.add(registerValues.last().plus(amount))
                }
            }
        }

        return listOf(20, 60, 100, 140, 180, 220)
            // registerValues represents AFTER the tick. Applying -1 here to get DURING the tick.
            .onEach { println("Cycle $it - X is ${registerValues[it - 1]}") }
            .sumOf { registerValues[it - 1] * it }
    }

    fun part2(instructions: List<String>) {
        val registerValues = mutableListOf(1) // represents the value AFTER the tick of the index
        instructions.forEach { instruction ->
            when {
                instruction == "noop"          -> registerValues.add(registerValues.last())
                instruction.startsWith("addx") -> {
                    val amount = instruction.substringAfter(" ").toInt()
                    registerValues.add(registerValues.last())
                    registerValues.add(registerValues.last().plus(amount))
                }
            }
        }

        val pixels = mutableListOf<Boolean>()

        registerValues.chunked(40).forEachIndexed { startingAt, values ->
            values.forEachIndexed { x, horizontalPosition ->
                val cycle = startingAt + horizontalPosition
                pixels.add(x in (cycle - 1 - startingAt)..(cycle + 1 - startingAt))
            }
        }

        pixels.joinToString("") { if (it) "#" else " " }.chunked(40).forEach(::println)

    }

    val testInput = readInput("Day10_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
