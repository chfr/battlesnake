package net.chfr.snake

import kotlin.math.absoluteValue


fun distance(a: Point, b: Point): Int {
    return (a.x - b.x).absoluteValue + (a.y - b.y).absoluteValue
}

fun moveTowards(from: Point, to: Point): Move {
    return if (from.x < to.x) {
        Move.Right
    } else if (from.x > to.x) {
        Move.Left
    } else if (from.y < to.y) {
        Move.Down
    } else {
        Move.Up
    }
}

fun closestFood(me: Snake, board: Board): Point? {
    val head = me.body.first()

    return board.food.minBy { food ->
        distance(head, food)
    }
}

fun applyMove(point: Point, move: Move) = when (move) {
    Move.Up -> point.copy(y = point.y - 1)
    Move.Down -> point.copy(y = point.y + 1)
    Move.Left -> point.copy(x = point.x - 1)
    Move.Right -> point.copy(x = point.x + 1)
}

fun randomValidMove(board: Board, me: Snake, enemies: List<Snake>): Move {
    val moves = Move.values().toList().shuffled()

    val moveOrNull = moves.firstOrNull { move ->
        isValidMove(board, me, enemies, move)
    }

    return when (moveOrNull) {
        null -> {
            println("No valid moves! Goodbye cruel world")
            Move.Up
        }
        else -> moveOrNull
    }
}

fun isValidMove(board: Board, me: Snake, enemies: List<Snake>, move: Move): Boolean {
    val head = me.body.first()
    val newHead = applyMove(head, move)


    val occupiedSquares = (listOf(me) + enemies).flatMap { it.body }.toSet()

    val squareOccupied = newHead in occupiedSquares
    val outOutBounds = !board.isWithinBounds(newHead)

    println("Moved ${move.label}, from $head to $newHead... Occupied: $squareOccupied, outOfBounds: $outOutBounds")

    return !(squareOccupied || outOutBounds)
}

fun nextMove(
    board: Board,
    me: Snake,
    enemies: List<Snake>
): Move {
    return when (val food = closestFood(me, board)) {
        null -> randomValidMove(board, me, enemies)
        else -> {
            val potentialMove = moveTowards(me.body.first(), food)
            if (isValidMove(board, me, enemies, potentialMove)) {
                potentialMove
            } else {
                randomValidMove(board, me, enemies)
            }
        }
    }
}
