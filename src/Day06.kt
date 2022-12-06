fun main() {

    class Signal(
        private val value: String,
        private val packetMarkerSize: Int = 4,
        private val messageMarkerSize: Int = 14
    ) {
        fun packetMarkerCharCount() = markerCharCount(packetMarkerSize)
        fun messageMarkerCharCount() = markerCharCount(messageMarkerSize)

        private fun markerCharCount(size: Int) = value.windowed(size).indexOfFirst { it.allCharsAreUnique() } + size
        private fun String.allCharsAreUnique() = toCharArray().distinct().size == length
    }

    fun part1(input: String): Int = Signal(input).packetMarkerCharCount()
    fun part2(input: String): Int = Signal(input).messageMarkerCharCount()

    check(part1("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 7)
    check(part1("bvwbjplbgvbhsrlpgdmjqwftvncz") == 5)
    check(part1("nppdvjthqldpwncqszvftbrmjlhg") == 6)
    check(part1("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 10)
    check(part1("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 11)

    check(part2("mjqjpqmgbljsphdztnvjfqwrcgsmlb") == 19)
    check(part2("bvwbjplbgvbhsrlpgdmjqwftvncz") == 23)
    check(part2("nppdvjthqldpwncqszvftbrmjlhg") == 23)
    check(part2("nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg") == 29)
    check(part2("zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw") == 26)

    val input = readInputAsText("Day06")
    println(part1(input))
    println(part2(input))
}
