private enum class Outcome(val score: Int) {
    WIN(6), DRAW(3), LOSE(0);

    fun inverted(): Outcome = when (this) {
        WIN -> LOSE; DRAW -> DRAW; LOSE -> WIN
    }
}

private sealed class Shape(val score: Int) {
    object Rock : Shape(1)
    object Paper : Shape(2)
    object Scissors : Shape(3)

    fun evaluate(other: Shape): Outcome = when {
        this == other                      -> Outcome.DRAW
        this == Rock && other == Scissors  -> Outcome.WIN
        this == Scissors && other == Paper -> Outcome.WIN
        this == Paper && other == Rock     -> Outcome.WIN
        else                               -> Outcome.LOSE
    }
}

private operator fun <T1, T2> List<T1>.times(other: List<T2>): List<Pair<T1, T2>> =
    buildList { this@times.forEach { left -> other.forEach { right -> add(left to right) } } }

private object Lookup {
    private val scenarios = buildList {
        val shapes = listOf(Shape.Rock, Shape.Paper, Shape.Scissors)
        (shapes * shapes).forEach { (shape1, shape2) -> add(Round(shape1, shape2)) }
    }

    fun find(shape1: Shape, shape2: Shape): Round =
        scenarios.single { it.shape1 == shape1 && it.shape2 == shape2 }

    fun find(shape1: Shape, outcome: Outcome): Round =
        scenarios.single { it.shape1 == shape1 && it.result.outcome2 == outcome }
}

private fun shapeFromCode(code: String) = when (code) {
    "A", "X" -> Shape.Rock
    "B", "Y" -> Shape.Paper
    "C", "Z" -> Shape.Scissors
    else     -> throw IllegalArgumentException()
}

private fun outcomeFromCode(code: String) = when (code) {
    "X"  -> Outcome.LOSE
    "Y"  -> Outcome.DRAW
    "Z"  -> Outcome.WIN
    else -> throw IllegalArgumentException()
}

private data class Tournament(val rounds: List<Round>) {
    val totalScore2 = rounds.sumOf { it.shape2.score + it.result.outcome2.score }
}

private data class Round(val shape1: Shape, val shape2: Shape) {
    val result: Result = shape1.evaluate(shape2).let { outcome1 -> Result(outcome1, outcome1.inverted()) }
}

private data class Result(val outcome1: Outcome, val outcome2: Outcome)

fun main() {
    fun parse(input: List<String>, transform: (String, String) -> Round): Tournament =
        input
            .map { it.split(" ") }
            .map { transform(it[0], it[1]) }
            .let(::Tournament)

    fun part1(input: List<String>): Int =
        parse(input) { code1, code2 -> Lookup.find(shapeFromCode(code1), shapeFromCode(code2)) }.totalScore2

    fun part2(input: List<String>): Int =
        parse(input) { code1, code2 -> Lookup.find(shapeFromCode(code1), outcomeFromCode(code2)) }.totalScore2

    val testInput = readInput("Day02_test")
    check(part1(testInput) == 15)

    val input = readInput("Day02")
    println(part1(input))
    println(part2(input))
}
