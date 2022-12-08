private class Node private constructor(val name: String, val intrinsicSize: Int? = null, val parent: Node? = null) : Iterable<Node> {
    private val children: MutableList<Node> = mutableListOf()
    val totalSize: Int get() = (intrinsicSize ?: 0) + children.sumOf { it.totalSize }

    operator fun get(childName: String) = children.first { it.name == childName }
    fun addChild(node: Node) = children.add(node)
    val isDirectory get() = intrinsicSize == null

    override fun iterator(): Iterator<Node> = iterator { yield(this@Node); children.forEach { yieldAll(it.iterator()) } }
    override fun toString(): String = "$name " + if (isDirectory) "(dir)" else "(file, size=$intrinsicSize)"

    companion object {
        fun file(name: String, size: Int, parent: Node) = Node(name, size, parent)
        fun directory(name: String, parent: Node) = Node(name, parent = parent)
        fun root() = Node("/")
    }
}

private fun parse(input: List<String>): Node {
    val cd = Regex("\\$ cd (.*)")
    val dir = Regex("dir (.*)")
    val file = Regex("(\\d+) (.*)")

    val root = Node.root()
    var current = root

    fun onDirectoryDiscovered(name: String) = current.addChild(Node.directory(name, current))
    fun onFileDiscovered(name: String, size: Int) = current.addChild(Node.file(name, size, current))
    fun onDirectoryChanged(name: String) {
        current = when (name) {
            "/" -> root; ".." -> current.parent!!; else -> current[name]
        }
    }

    fun Regex.onMatch(line: String, block: (List<String>) -> Unit) = find(line)?.let { block(it.groupValues) }

    input.forEach { line ->
        dir.onMatch(line) { onDirectoryDiscovered(it[1]) }
        file.onMatch(line) { onFileDiscovered(it[2], it[1].toInt()) }
        cd.onMatch(line) { onDirectoryChanged(it[1]) }
    }

    return root
}

fun main() {
    fun part1(input: List<String>): Int = parse(input)
        .filter { it.isDirectory && it.totalSize <= 100_000 }
        .sumOf { it.totalSize }

    fun part2(input: List<String>): Int = with(parse(input)) {
        val minimumToDelete = 30_000_000 - (70_000_000 - totalSize)
        return filter { it.isDirectory }.sortedBy { it.totalSize }.first { it.totalSize >= minimumToDelete }.totalSize
    }

    val testInput = readInput("Day07_test")
    check(part1(testInput) == 95437)
    check(part2(testInput) == 24_933_642)

    val input = readInput("Day07")
    println(part1(input))
    println(part2(input))
}
