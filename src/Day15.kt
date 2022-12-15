import lib.Coordinates
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

fun IntRange.clamp(minimum: Int, maximum: Int) = max(minimum, first)..min(maximum, last)

fun Set<IntRange>.consolidate(): Set<IntRange> {
    var result = this
    var lastCount: Int

    do {
        lastCount = result.size

        result = result.fold(setOf()) { acc, incoming ->
            if (acc.isEmpty()) {
                setOf(incoming)
            } else {
                buildSet {
                    var hasConsolidated = false
                    for (range in acc) {
                        val isIncomingInThis = incoming.first - 1 in range || incoming.last + 1 in range
                        val isThisInIncoming = range.first - 1 in incoming || range.last + 1 in incoming
                        if (isIncomingInThis || isThisInIncoming) {
                            add(IntRange(min(range.first, incoming.first), max(range.last, incoming.last)))
                            hasConsolidated = true
                        } else {
                            add(range)
                        }
                    }
                    if (!hasConsolidated) add(incoming)
                }
            }
        }
    } while (result.size != lastCount && result.size > 1)

    return result
}

data class Sensor(val coordinates: Coordinates, val closestBeacon: Coordinates) {
    private val distance = coordinates.manhattanDistanceTo(closestBeacon)

    fun positionsWithNoBeacon(y: Int): IntRange? {
        val yDistanceFromSensor = (coordinates.y - y).absoluteValue
        if (yDistanceFromSensor > distance) return null

        val xDistance = (distance - yDistanceFromSensor).absoluteValue
        val left = coordinates.x - xDistance
        val right = coordinates.x + xDistance

        return if (right - left > 0) left..right else null
    }
}

private fun parse(input: List<String>): Set<Sensor> {
    val pattern = Regex("Sensor at x=(-?\\d+), y=(-?\\d+): closest beacon is at x=(-?\\d+), y=(-?\\d+)")

    return buildSet {
        input.forEach { line ->
            pattern.find(line)?.run {
                add(Sensor(Coordinates(groupValues[1].toInt(), groupValues[2].toInt()), Coordinates(groupValues[3].toInt(), groupValues[4].toInt())))
            }
        }
    }
}

fun main() {
    fun part1(input: List<String>, row: Int): Int {
        val sensors = parse(input)
        val reports = sensors.mapNotNull { it.positionsWithNoBeacon(row) }
        val merged = reports.toSet().consolidate()
        val noBeaconPositionCount = merged.sumOf { it.count() }

        val sensorsAndBeaconsOnRow = sensors
            .flatMap { listOf(it.coordinates, it.closestBeacon) }
            .distinct()
            .filter { it.y == row }
            .filter { occupiedCoordinate -> merged.any { occupiedCoordinate.x in it } }

        return noBeaconPositionCount - sensorsAndBeaconsOnRow.count()
    }

    fun part2(input: List<String>, range: IntRange): Long {
        val sensors = parse(input)
        val coordinates = range
            .map { y ->
                sensors
                    .mapNotNull { it.positionsWithNoBeacon(y) }
                    .map { it.clamp(range.first, range.last) }
                    .toSet()
                    .consolidate()
            }
            .mapIndexedNotNull { index, ranges ->
                if (ranges.count() == 2) {
                    Coordinates(ranges.toList().first().last + 1, index)
                } else null
            }
            .single()

        return (coordinates.x * 4000000L) + coordinates.y
    }

    val testInput = readInput("Day15_test")
    println(part1(testInput, 10))
    println(part2(testInput, 0..20))

    val input = readInput("Day15")
    println(part1(input, 2_000_000))
    println(part2(input, 0..4_000_000))
}
