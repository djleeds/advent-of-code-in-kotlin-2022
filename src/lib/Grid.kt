package lib

import kotlin.math.max
import kotlin.math.min

class Grid<T>(private val cells: MutableMap<Coordinates, T>, private val default: T) {
    private val flat get() = cells.map { (key, value) -> Cell(key, value) }

    private val xMin get() = flat.minOf { it.coordinates.x }
    private val xMax get() = flat.maxOf { it.coordinates.x }
    private val yMin get() = flat.minOf { it.coordinates.y }
    private val yMax get() = flat.maxOf { it.coordinates.y }

    val bounds: Bounds get() = Bounds(xMin..xMax, yMin..yMax)

    operator fun get(x: Int, y: Int): T = cells.getOrDefault(Coordinates(x, y), default)
    operator fun get(coordinates: Coordinates): T = cells.getOrDefault(coordinates, default)
    operator fun set(coordinates: Coordinates, item: T) = cells.put(coordinates, item)
    operator fun set(x: Int, y: Int, item: T) = cells.put(Coordinates(x, y), item)

    fun find(predicate: (Cell<T>) -> Boolean) = flat.first(predicate)

    fun print(mapping: (T) -> Char) {
        bounds.northSouthBounds.forEach { y ->
            bounds.eastWestBounds.forEach { x ->
                print(mapping(this[x, y]))
            }
            println()
        }
    }

    class Bounds(val eastWestBounds: IntRange, val northSouthBounds: IntRange) {
        operator fun contains(coordinates: Coordinates) = coordinates.x in eastWestBounds && coordinates.y in northSouthBounds
    }

    companion object {
        fun <T> parseCharacters(input: List<String>, transform: (Char) -> T, default: T): Grid<T> {
            val map = mutableMapOf<Coordinates, T>()
            input.mapIndexed { y, row ->
                row.mapIndexed { x, char ->
                    map.put(Coordinates(x, y), transform(char))
                }
            }
            return Grid(map, default)
        }
    }
}

data class Cell<T>(val coordinates: Coordinates, val item: T)

data class Coordinates(val x: Int, val y: Int) {
    val adjacent: List<Coordinates> by lazy {
        listOf(step(Direction.NORTH), step(Direction.SOUTH), step(Direction.EAST), step(Direction.WEST))
    }

    fun step(direction: Direction) = when (direction) {
        Direction.NORTH     -> copy(y = y - 1)
        Direction.SOUTH     -> copy(y = y + 1)
        Direction.EAST      -> copy(x = x + 1)
        Direction.WEST      -> copy(x = x - 1)
        Direction.NORTHWEST -> copy(x = x - 1, y = y - 1)
        Direction.NORTHEAST -> copy(x = x + 1, y = y - 1)
        Direction.SOUTHWEST -> copy(x = x - 1, y = y + 1)
        Direction.SOUTHEAST -> copy(x = x + 1, y = y + 1)
    }

    infix fun through(other: Coordinates): Iterable<Coordinates> = Iterable {
        val (minimum, maximum) = canonicalizedWith(other)
        iterator {
            for (x in minimum.x..maximum.x) {
                for (y in minimum.y..maximum.y) {
                    yield(Coordinates(x, y))
                }
            }
        }
    }

    private fun canonicalizedWith(other: Coordinates): Pair<Coordinates, Coordinates> =
        Coordinates(min(x, other.x), min(y, other.y)) to Coordinates(max(x, other.x), max(y, other.y))

    fun manhattanDistanceTo(other: Coordinates): Int {
        val (minimum, maximum) = canonicalizedWith(other)
        return (maximum.x - minimum.x) + (maximum.y - minimum.y)
    }

    companion object {
        fun parse(string: String) =
            string.split(",").map { it.trim() }.let { Coordinates(it[0].toInt(), it[1].toInt()) }
    }
}

operator fun <T> List<List<T>>.get(coordinates: Coordinates): T = this[coordinates.y][coordinates.x]

enum class Direction {
    NORTH, SOUTH, EAST, WEST, NORTHWEST, NORTHEAST, SOUTHWEST, SOUTHEAST;

    companion object {
        val UP = NORTH
        val DOWN = SOUTH
        val RIGHT = EAST
        val LEFT = WEST
        val UP_LEFT = NORTHWEST
        val UP_RIGHT = NORTHEAST
        val DOWN_LEFT = SOUTHWEST
        val DOWN_RIGHT = SOUTHEAST
    }
}
