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

    val history: MutableList<Int> = mutableListOf(0, 1)
    private val x: Int get() = history.lastOrNull() ?: 1
    private fun tick(value: Int = x) = history.add(value)

    fun execute(program: List<Instruction>) = program.forEach { instruction ->
        repeat(instruction.cycles - 1) { tick() }
        tick(instruction.operation(x))
    }

    suspend fun execute2(program: List<Instruction>) {
        println("A")
        signals.send(history.lastIndex to x)
        println("B")
        program.forEach { instruction ->
            repeat(instruction.cycles - 1) { tick(); signals.send(history.lastIndex to x) }
            tick(instruction.operation(x)); signals.send(history.lastIndex to x)
        }
        println("C")
        signals.close()
        println("D")
    }
}

data class Sprite(private val width: Int, private val position: Int) {
    private val reach = (width - 1) / 2
    val occupiedPixels: IntRange get() = (position - reach)..(position + reach)
    fun movedTo(position: Int) = Sprite(width, position)
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

class CRT2(private val width: Int, height: Int, private val signals: Channel<Pair<Int, Int>>) {
    private var sprite: Sprite = Sprite(3, 0)
    private val pixels: MutableList<Boolean> = MutableList((width * height) + 100) { false }

    fun tick(cycle: Int, sprite: Sprite) {
        val renderingAt = cycle.rem(width)
        pixels[cycle] = renderingAt in sprite.occupiedPixels
    }

    suspend fun listen() = signals.consumeEach { (cycle, x) ->
        println("$cycle = $x")
        sprite = sprite.movedTo(x)
        pixels[cycle] = cycle.rem(width) in sprite.occupiedPixels
    }

    fun render(on: String = "#", off: String = ".") {
        pixels.joinToString("") { if (it) on else off }.chunked(40).forEach(::println)
    }
}


fun main() = runBlocking {
    fun part1(instructions: List<String>): Int {
        val cpu = CPU().apply { execute(instructions.map(Instruction::parse)) }
        return listOf(20, 60, 100, 140, 180, 220).sumOf { cpu.history[it] * it }
    }

    suspend fun part1b(instructions: List<String>): Int {
        val cpu = CPU()

        launch { cpu.execute2(instructions.map(Instruction::parse)) }

        return cpu.signals.toList()
            .filter { (cycle, _) -> cycle in listOf(20, 60, 100, 140, 180, 220) }
            .sumOf { (cycle, x) -> cycle * x }
    }

    fun part2(instructions: List<String>) {
        val crt = CRT(40, 6)

        CPU()
            .apply { execute(instructions.map(Instruction::parse)) }
            .history
            .forEachIndexed { cycle, x -> crt.tick(cycle, Sprite(3, x)) }

        crt.render()
    }

    suspend fun part2b(instructions: List<String>) {
        val cpu = CPU()
        val crt = CRT2(40, 6, cpu.signals)

        launch { cpu.execute2(instructions.map(Instruction::parse)) }

        crt.listen()
        crt.render()
    }


    val testInput = readInput("Day10_test")
    println(part1(testInput))
    println(part1b(testInput))
    println(part2(testInput))
    println(part2b(testInput))
//
//    val input = readInput("Day10")
//    println(part1(input))
//    println(part2(input))
}
