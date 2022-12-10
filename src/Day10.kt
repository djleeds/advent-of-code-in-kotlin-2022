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
    val xHistory: MutableList<Int> = mutableListOf(1)
    private val x: Int get() = xHistory.last()
    private fun tick(value: Int = x) = xHistory.add(value)

    fun process(instruction: Instruction) {
        repeat(instruction.cycles - 1) { tick() }
        tick(instruction.operation(x))
    }
}

class Sprite(val width: Int, val position: Int) {
    val occupied: IntRange = (position - 1)..(position + 1)
}

class CRT(val width: Int, val height: Int) {
    val pixels: MutableList<Boolean> = MutableList((width * height) + 1) { false }
    fun tick(cycle: Int, sprite: Sprite) {
        val renderingAt = cycle.rem(width)
        pixels[cycle] = renderingAt in sprite.occupied
    }
}

fun main() {
    fun part1(instructions: List<String>): Int {
        val cpu = CPU()
        instructions.map(Instruction::parse).forEach(cpu::process)

        return listOf(20, 60, 100, 140, 180, 220)
            // registerValues represents AFTER the tick. Applying -1 here to get DURING the tick.
            .onEach { println("Cycle $it - X is ${cpu.xHistory[it - 1]}") }
            .sumOf { cpu.xHistory[it - 1] * it }
    }

    fun part2(instructions: List<String>) {
        val cpu = CPU()
        instructions.map(Instruction::parse).forEach(cpu::process)

        val crt = CRT(40, 6)
        cpu.xHistory.forEachIndexed { cycle, x ->
            val sprite = Sprite(3, x)
            crt.tick(cycle, sprite)
        }

        crt.pixels.joinToString("") { if (it) "#" else " " }.chunked(40).forEach(::println)

    }

    val testInput = readInput("Day10_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day10")
    println(part1(input))
    println(part2(input))
}
