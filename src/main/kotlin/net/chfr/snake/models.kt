package net.chfr.snake

data class Point(
    val x: Int,
    val y: Int
)

data class Snake(
    val body: List<Point>
) {
    fun contains(point: Point): Boolean {
        return point in body
    }
}

data class Board(
    val width: Int,
    val height: Int,
    val food: List<Point>
) {
    fun isWithinBounds(point: Point) = point.x in 0 until width && point.y in 0 until height
}