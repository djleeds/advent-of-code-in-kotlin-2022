@JvmInline value class Tree(val height: Int)

class Forest(private val width: Int, private val height: Int) {
    private val trees: Array<Array<Tree?>> = Array(width) { Array(height) { null } }

    fun addTree(x: Int, y: Int, height: Int) {
        trees[x][y] = Tree(height)
    }

    fun coordinates() = Iterable {
        iterator { (0 until height).forEach { y -> (0 until width).forEach { x -> yield(Coordinates8(x, y)) } } }
    }

    fun isVisible(coordinates: Coordinates8) = Direction.values().any { isVisible(coordinates, it) }
    fun scenicScore(coordinates: Coordinates8) = Direction.values().map { scenicScore(coordinates, it) }.fold(1) { acc, it -> acc * it }

    private operator fun Array<Array<Tree?>>.get(coordinates: Coordinates8): Tree? =
        runCatching { this[coordinates.x][coordinates.y] }.getOrNull()

    private fun isVisible(coordinates: Coordinates8, direction: Direction): Boolean {
        val height = trees[coordinates]!!.height
        return treeLine(coordinates, direction).firstOrNull { it.height >= height } == null
    }

    private fun scenicScore(coordinates: Coordinates8, direction: Direction): Int {
        val height = trees[coordinates]!!.height
        val treeLine = treeLine(coordinates, direction)
        return treeLine.indexOfFirst { it.height >= height }.takeIf { it != -1 }?.plus(1) ?: treeLine.count()
    }

    private fun treeLine(coordinates: Coordinates8, direction: Direction) = Iterable {
        iterator {
            var coords = coordinates.step(direction)
            while (coords.x in 0 until width && coords.y in 0 until height) {
                trees[coords]?.let { tree -> yield(tree); coords = coords.step(direction) }
            }
        }
    }
}

data class Coordinates8(val x: Int, val y: Int) {
    fun step(direction: Direction) = when (direction) {
        Direction.NORTH -> copy(y = y - 1)
        Direction.SOUTH -> copy(y = y + 1)
        Direction.EAST  -> copy(x = x + 1)
        Direction.WEST  -> copy(x = x - 1)
    }
}

enum class Direction { NORTH, SOUTH, EAST, WEST }

private fun parse(input: List<String>) =
    Forest(input.first().length, input.size).apply {
        input.forEachIndexed { y, row ->
            row.forEachIndexed { x, height ->
                addTree(x, y, height.toString().toInt())
            }
        }
    }

fun main() {
    fun part1(forest: Forest): Int = forest.coordinates().count(forest::isVisible)
    fun part2(forest: Forest): Int = forest.coordinates().maxOf(forest::scenicScore)

    var forest = parse(readInput("Day08_test"))
    check(part1(forest) == 21)
    check(part2(forest) == 8)

    forest = parse(readInput("Day08"))
    println(part1(forest))
    println(part2(forest))
}
