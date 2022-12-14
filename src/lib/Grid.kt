package lib

import kotlin.math.max
import kotlin.math.min

class Grid<T>(private val cells: List<List<Cell<T>>>) {
    private val flat = cells.flatten()
    val bounds = Bounds(cells.first().indices, cells.indices)

    operator fun get(x: Int, y: Int) = cells[y][x]
    operator fun get(coordinates: Coordinates) = cells[coordinates.y][coordinates.x]

    fun find(predicate: (Cell<T>) -> Boolean) = flat.first(predicate)

    class Bounds(private val eastWestBounds: IntRange, private val northSouthBounds: IntRange) {
        operator fun contains(coordinates: Coordinates) = coordinates.x in eastWestBounds && coordinates.y in northSouthBounds
    }

    companion object {
        fun <T> parseCharacters(input: List<String>, transform: (Char) -> T): Grid<T> =
            input.mapIndexed { y, row ->
                row.mapIndexed { x, char ->
                    Cell(Coordinates(x, y), transform(char))
                }
            }.let(::Grid)

        fun <T> parseCharacters(input: String, width: Int, transform: (Char) -> T): Grid<T> =
            parseCharacters(input.chunked(width), transform)

//        fun <T> createEmpty(width: Int, height: Int): Grid<T> =
//            Grid<T>(MutableList(height) { MutableList(width) {  } })
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
        val from = this
        iterator {
            for (x in min(from.x, other.x)..max(from.x, other.x)) {
                for (y in min(from.y, other.y)..max(from.y, other.y)) {
                    yield(Coordinates(x, y))
                }
            }
        }
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
