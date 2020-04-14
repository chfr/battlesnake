package net.chfr.snake


fun applyMove(point: Point, move: Move) = when (move) {
    Move.Up -> point.copy(y = point.y - 1)
    Move.Down -> point.copy(y = point.y + 1)
    Move.Left -> point.copy(x = point.x - 1)
    Move.Right -> point.copy(x = point.x - 1)
}

fun isValidMove(board: Board, me: Snake, enemies: List<Snake>, move: Move): Boolean {
    val head = me.body.first()
    val newHead = applyMove(head, move)

    val occupiedSquares = (listOf(me) + enemies).flatMap { it.body }.toSet()

    return board.isWithinBounds(newHead) && newHead !in occupiedSquares
}

fun nextMove(
    board: Board,
    me: Snake,
    enemies: List<Snake>
): Move {
    val moves = Move.values().toList().shuffled()

    return moves.firstOrNull { move ->
        isValidMove(board, me, enemies, move)
    } ?: Move.Down
}