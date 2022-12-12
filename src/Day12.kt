import java.util.*

data class Coordinates(val x: Int, val y: Int) {
    val adjacent: List<Coordinates> by lazy {
        listOf(Coordinates(x - 1, y), Coordinates(x + 1, y), Coordinates(x, y - 1), Coordinates(x, y + 1))
    }
}

enum class PlotType {
    START, END, OTHER;

    companion object {
        fun from(char: Char) = when (char) {
            'S' -> START; 'E' -> END; else -> OTHER
        }
    }
}

data class Plot(val coordinates: Coordinates, val height: Int, val type: PlotType) {
    companion object {
        fun from(coordinates: Coordinates, char: Char) = Plot(
            coordinates,
            when (char) {
                'S' -> 0; 'E' -> 'z' - 'a'; else -> char - 'a'
            },
            PlotType.from(char)
        )
    }
}

data class Step(val plot: Plot, val distance: Int = 0) {
    infix fun toward(plot: Plot): Step = Step(plot, distance + 1)
}

class Terrain(private val plots: List<List<Plot>>) {
    private val bounds = Bounds(plots.first().indices, plots.indices)
    private val endPoint = findPlot { it.type == PlotType.END }

    fun shortestDistance(isDestination: (Plot) -> Boolean): Int {
        val visitedPlots = mutableSetOf(endPoint)
        val steps: Queue<Step> = LinkedList<Step>().apply { add(Step(endPoint)) }

        while (steps.isNotEmpty()) {
            val step = steps.poll()
            log { "Inspecting $step - Queue: $steps" }
            if (isDestination(step.plot)) return step.distance

            step.plot.coordinates.adjacent
                .filter { it in bounds }
                .map { plots[it] }
                .filter { it.height >= step.plot.height - 1 }
                .filter { it !in visitedPlots }
                .forEach {
                    steps.add(step toward it)
                    visitedPlots.add(it)
                }
        }

        throw IllegalStateException("Could not find a path that meets the criteria.")
    }

    private fun findPlot(predicate: (Plot) -> Boolean) = plots.flatten().first(predicate)

    private operator fun <T> List<List<T>>.get(coordinates: Coordinates) = this[coordinates.y][coordinates.x]

    private class Bounds(private val eastWestBounds: IntRange, private val northSouthBounds: IntRange) {
        operator fun contains(coordinates: Coordinates) = coordinates.x in eastWestBounds && coordinates.y in northSouthBounds
    }

    companion object {
        fun parse(input: List<String>) =
            input.mapIndexed { y, row ->
                row.mapIndexed { x, char ->
                    Plot.from(Coordinates(x, y), char)
                }
            }.let(::Terrain)
    }
}

fun main() {
    fun part1(input: List<String>): Int =
        Terrain.parse(input).shortestDistance { it.type == PlotType.START }

    fun part2(input: List<String>): Int =
        Terrain.parse(input).shortestDistance { it.height == 0 }

    val testInput = readInput("Day12_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day12")
    println(part1(input))
    println(part2(input))
}
