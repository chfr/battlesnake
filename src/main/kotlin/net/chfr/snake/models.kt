package net.chfr.snake

data class Point(
    val x: Int,
    val y: Int
)

data class Snake(
    val body: List<Point>
) {
    fun isWithin(point: Point): Boolean {
        return point in body
    }
}

data class Board(
    val width: Int,
    val height: Int,
    val food: List<Point>
)