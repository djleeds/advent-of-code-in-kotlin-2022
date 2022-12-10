sealed class Instruction(val cycles: Int, val operation: (previousX: Int) -> Int) {
    class NoOp : Instruction(1, { it })
    class AddX(private val amount: Int) : Instruction(2, { it + amount })
    companion object {
        fun parse(line: String) = when {
            line == "noop"          -> NoOp()
            line.startsWith("addx") -> AddX(line.substringAfter(" ").toInt())
            else                    -> throw IllegalArgumentException()
        }
    }
}

class CPU {
    val history: MutableList<Int> = mutableListOf(1)
    private val x: Int get() = history.last()
    private fun tick(value: Int = x) = history.add(value)

    fun execute(program: List<Instruction>) = program.forEach { instruction ->
        repeat(instruction.cycles - 1) { tick() }
        tick(instruction.operation(x))
    }
}

class Sprite(width: Int, position: Int) {
    private val reach = (width - 1) / 2
    val occupiedPixels: IntRange = (position - reach)..(position + reach)
}

class CRT(private val width: Int, height: Int) {
    private val pixels: MutableList<Boolean> = MutableList((width * height) + 1) { false }

    fun tick(cycle: Int, sprite: Sprite) {
        val renderingAt = cycle.rem(width)
        pixels[cycle] = renderingAt in sprite.occupiedPixels
    }

    fun render(on: String = "#", off: String = ".") =
        pixels.joinToString("") { if (it) on else off }.chunked(40).forEach(::println)
}

fun main() {
    fun part1(instructions: List<String>): Int {
        val cpu = CPU().apply { execute(instructions.map(Instruction::parse)) }
        return listOf(20, 60, 100, 140, 180, 220).sumOf { cpu.history[it - 1] * it }
    }

    fun part2(instructions: List<String>) {
        val crt = CRT(40, 6)

        CPU()
            .apply { execute(instructions.map(Instruction::parse)) }
            .history
            .forEachIndexed { cycle, x -> crt.tick(cycle, Sprite(3, x)) }

        crt.render()
    }

    val testInput = readInput("Day10_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
