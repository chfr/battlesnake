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

fun moveSnake(snake: Snake, move: Move): Snake {
    val newHead = applyMove(snake.body.first(), move)

    return snake.copy(
        body = listOf(newHead) + snake.body.dropLast(1)
    )
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

fun randomValidMove(board: Board, me: Snake, enemies: List<Snake>, except: Set<Move> = emptySet()): Move {
    val moves = Move.values().toList().shuffled()

    val moveOrNull = moves.firstOrNull { move ->
        move !in except && isValidMove(board, me, enemies, move)
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

    return !(squareOccupied || outOutBounds)
}

fun nextMove(
    board: Board,
    me: Snake,
    enemies: List<Snake>
): Move {
    println("I have ${me.health} hp")

    return if (isHungry(me, board)) {
        val food = closestFood(me, board)!!

        val potentialMove = moveTowards(me.body.first(), food)
        if (isValidMove(board, me, enemies, potentialMove)) {
            if (likelyHeadOn(me, enemies, potentialMove)) {
                println("Potential head on! Abort!")
                randomValidMove(board, me, enemies, except = setOf(potentialMove))
            }
            potentialMove
        } else {
            println("Couldn't move towards food!")
            randomValidMove(board, me, enemies)
        }
    } else {
        straightLines(me, board, enemies)
    }
}

fun straightLines(me: Snake, board: Board, enemies: List<Snake>): Move {
    val direction = movingDirection(me)

    return if (isValidMove(board, me, enemies, direction)) {
        if (likelyHeadOn(me, enemies, direction)) {
            println("Potential head on! Abort!")
            randomValidMove(board, me, enemies, except = setOf(direction))
        } else {
            direction
        }
    } else {
        // TODO don't do random?
        randomValidMove(board, me, enemies)
    }
}

/**
 * Direction in which the snake is moving
 */
fun movingDirection(snake: Snake): Move {
    if (snake.body.size == 1)
        return Move.Up

    val head = snake.body.first()
    val body = snake.body[1]

    return if (head.copy(x = head.x - 1) == body) {
        Move.Right
    } else if (head.copy(x = head.x + 1) == body) {
        Move.Left
    } else if (head.copy(y = head.y - 1) == body) {
        Move.Down
    } else {
        Move.Up
    }
}

fun likelyHeadOn(me: Snake, enemies: List<Snake>, direction: Move): Boolean {
    val enemyHeads = enemies.map { it.body.first() }.toSet()
    val oneMoveAhead = moveSnake(me, direction)
    val twoMovesAhead = moveSnake(oneMoveAhead, direction)

    return oneMoveAhead.body.first() in enemyHeads || twoMovesAhead.body.first() in enemyHeads
}

fun isHungry(me: Snake, board: Board): Boolean {
    if (me.body.size == 1)
        return true
    if (me.health < (board.height + board.width) / 2) {
        return true
    }

    return when (val closestFood = closestFood(me, board)) {
        null -> false
        else -> {
            val distance = distance(closestFood, me.body.first())
            // If I can only travel thrice the distance to the closest food, it's time to hunt!
            if (distance * 3 > me.health) {
                println("ME HUNGRY! Closest is $distance away")
                return true
            }
            return false
        }
    }
}
