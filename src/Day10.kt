import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

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
    val signals = Channel<Pair<Int, Int>>()

    private var x: Int = 1
    private var cycle: Int = 1

    private suspend fun tick(value: Int = x) {
        cycle++
        x = value
        signals.send(cycle to value)
    }

    suspend fun execute(program: List<Instruction>) {
        signals.send(cycle to x)
        program.forEach { instruction ->
            repeat(instruction.cycles - 1) { tick(); }
            tick(instruction.operation(x))
        }
        signals.close()
    }
}

data class Sprite(private val width: Int, private val position: Int) {
    private val reach = (width - 1) / 2
    val occupiedPixels: IntRange get() = (position - reach)..(position + reach)
    fun movedTo(position: Int) = Sprite(width, position)
}

class CRT(private val width: Int, height: Int, private val signals: Channel<Pair<Int, Int>>) {
    private var sprite: Sprite = Sprite(3, 0)
    private val pixels: MutableList<Boolean> = MutableList((width * height) + 2) { false }

    suspend fun listen() = signals.consumeEach { (cycle, x) ->
        sprite = sprite.movedTo(x)
        pixels[cycle] = cycle.rem(width) in sprite.occupiedPixels
    }

    fun render(on: String = "#", off: String = ".") {
        pixels.joinToString("") { if (it) on else off }.windowed(40, 40).forEach(::println)
    }
}


fun main() = runBlocking {
    suspend fun part1(instructions: List<String>): Int {
        val cpu = CPU()

        launch { cpu.execute(instructions.map(Instruction::parse)) }

        return cpu.signals.toList()
            .filter { (cycle, _) -> cycle in listOf(20, 60, 100, 140, 180, 220) }
            .sumOf { (cycle, x) -> cycle * x }
    }

    suspend fun part2(instructions: List<String>) {
        val cpu = CPU()
        val crt = CRT(40, 6, cpu.signals)

        launch { cpu.execute(instructions.map(Instruction::parse)) }

        crt.listen()
        crt.render()
    }


    val testInput = readInput("Day10_test")
    println(part1(testInput))
    part2(testInput)

    val input = readInput("Day10")
    println(part1(input))
    part2(input)
}
