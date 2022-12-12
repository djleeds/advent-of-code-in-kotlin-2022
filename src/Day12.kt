import java.util.*

typealias Coordinates = Pair<Int, Int>

enum class PlotType {
    START, END, OTHER;

    companion object {
        fun fromChar(char: Char) = when (char) {
            'S' -> START; 'E' -> END; else -> OTHER
        }
    }
}

data class Plot(val x: Int, val y: Int, val height: Int, val type: PlotType) {
    val adjacentCoordinates: List<Coordinates> = listOf(x - 1 to y, x + 1 to y, x to y - 1, x to y + 1)

    companion object {
        fun from(x: Int, y: Int, char: Char) = Plot(
            x, y, when (char) {
                'S'  -> 0
                'E'  -> 'z' - 'a'
                else -> char - 'a'
            }, PlotType.fromChar(char)
        )
    }
}

data class Step(val plot: Plot, val distance: Int = 0) {
    fun toward(plot: Plot): Step = Step(plot, distance + 1)
}

class Terrain(private val plots: List<List<Plot>>) {
    private val eastWestBounds = plots.first().indices
    private val northSouthBounds = plots.indices
    private val endPoint = findPlot { it.type == PlotType.END }

    fun shortestDistance(isDestination: (Plot) -> Boolean): Int {
        val visitedPlots = mutableSetOf(endPoint)
        val steps: Queue<Step> = LinkedList<Step>().apply { add(Step(endPoint)) }

        while (steps.isNotEmpty()) {
            val step = steps.poll()
            if (isDestination(step.plot)) return step.distance

            log { "Visiting $step - $steps" }

            step.plot.adjacentCoordinates
                .mapNotNull(::getPlot)
                .filter { it.height >= step.plot.height - 1 }
                .filter { it !in visitedPlots }
                .forEach {
                    steps.add(step.toward(it))
                    visitedPlots.add(it)
                }
        }

        throw IllegalStateException("Could not find a path that meets the criteria.")
    }

    private fun getPlot(coordinates: Coordinates) =
        coordinates.takeIf { it.withinBounds() }?.let { (x, y) -> plots[y][x] }

    private fun Coordinates.withinBounds() =
        first in eastWestBounds && second in northSouthBounds

    private fun findPlot(predicate: (Plot) -> Boolean) =
        plots.flatten().first(predicate)

    companion object {
        fun parse(input: List<String>) =
            input.mapIndexed { y, row ->
                row.mapIndexed { x, char ->
                    Plot.from(x, y, char)
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
