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
    fun isWithinBounds(point: Point) = point.x >= 0 || point.x < width || point.y >= 0 || point.y < height
}