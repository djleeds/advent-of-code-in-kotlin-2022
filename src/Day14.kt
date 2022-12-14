import lib.Coordinates
import lib.Direction
import lib.get

enum class Material { AIR, ROCK, SAND }

private fun printGrid(grid: List<List<Material>>) {
    for (y in grid.indices) {
        println("")
        for (x in grid[y].indices) {
            when (grid[y][x]) {
                Material.AIR  -> print(".")
                Material.ROCK -> print("#")
                Material.SAND -> print("o")
            }
        }
    }
    println()
}

private fun parse(input: List<String>): MutableList<MutableList<Material>> {
    val coordinates = input.map { line -> line.split(" -> ").map { Coordinates.parse(it) } }
    val width = coordinates.maxOf { c -> c.maxOf { it.x } } * 2
    val height = coordinates.maxOf { c -> c.maxOf { it.y } } + 2

    val grid: MutableList<MutableList<Material>> = MutableList(height) { MutableList(width) { Material.AIR } }

    input.forEach { line ->
        line.split(" -> ").map { Coordinates.parse(it) }.zipWithNext().forEach {
            for (c in it.first through it.second) grid[c.y][c.x] = Material.ROCK
        }
    }

    return grid
}

private fun solve(
    grid: MutableList<MutableList<Material>>,
    emitterCoordinates: Coordinates,
    terminalCondition: (sand: Coordinates, isResting: Boolean) -> Boolean
): Int {
    var complete = false
    var sand = emitterCoordinates

    var sandCount = 0

    do {
        when (Material.AIR) {
            grid[sand.step(Direction.DOWN)]       -> sand = sand.step(Direction.DOWN)
            grid[sand.step(Direction.DOWN_LEFT)]  -> sand = sand.step(Direction.DOWN_LEFT)
            grid[sand.step(Direction.DOWN_RIGHT)] -> sand = sand.step(Direction.DOWN_RIGHT)
            else                                  -> {
                complete = terminalCondition(sand, true)
                grid[sand.y][sand.x] = Material.SAND
                sandCount++
                sand = emitterCoordinates
            }
        }
        complete = complete || terminalCondition(sand, false)
    } while (!complete)
    return sandCount
}

fun main() {
    val emitter = Coordinates(500, 0)

    fun part1(input: List<String>): Int {
        val grid = parse(input)
        val abyss = grid.lastIndex

        return solve(grid, emitter) { sand, _ -> sand.y == abyss }.also { if (debug) printGrid(grid) }
    }

    fun part2(input: List<String>): Int {
        val grid = parse(input).apply { add(MutableList(this[0].size) { Material.ROCK }) }

        return solve(grid, emitter) { sand, isResting -> isResting && sand == emitter }.also { if (debug) printGrid(grid) }
    }

    val testInput = readInput("Day14_test")
    println(part1(testInput))
    println(part2(testInput))

    val input = readInput("Day14")
    println(part1(input))
    println(part2(input))
}
