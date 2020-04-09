@file:Suppress("UNCHECKED_CAST")

package net.chfr.snake

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import io.javalin.Javalin
import io.javalin.http.Context

val mapper = jacksonObjectMapper()
typealias Json = Map<String, Any>

enum class Move(val label: String) {
    Up("up"),
    Down("down"),
    Left("left"),
    Right("right")
}

fun main(args: Array<String>) {
    val app = Javalin.create().start(getHerokuAssignedPort())
    app.get("/") { ctx -> ctx.result("Hello herokuuuu") }
    app.post("/ping") { ctx -> ctx.result("") }
    app.post("/start") { ctx -> ctx.result("") }
    app.post("/end") { ctx -> ctx.result("") }
    app.post("/move") { ctx -> move(ctx) }
}

fun move(context: Context) {
    val body = context.body()
    val bodyAsMap: Json = mapper.readValue(body)

    val boardAsMap = bodyAsMap.getValue("board") as Map<String, Any>
    val board = parseBoard(boardAsMap)
    val me = parseSnake(bodyAsMap.getValue("you") as Map<String, Any>)
    val enemies = (boardAsMap.getValue("snakes") as List<Map<String, Any>>).map {
        parseSnake(it)
    }

    val nextMove = nextMove(board, me, enemies)

    val response = mapOf(
        "move" to nextMove.label
    )
    context.json(response)
}

fun parseBoard(map: Json): Board {
    return Board(
        width = map.getValue("height") as Int,
        height = map.getValue("width") as Int,
        food = (map.getValue("food") as List<Map<String, Any>>).map {
            Point(
                x = it.getValue("x") as Int,
                y = it.getValue("y") as Int
            )
        }
    )
}

fun parseSnake(map: Json): Snake {
    val bodyPoints = map.getValue("body" ) as List<Map<String, Any>>

    return Snake(
        bodyPoints.map {
            Point(
                x = it.getValue("x") as Int,
                y = it.getValue("y") as Int
            )
        }
    )
}

fun getHerokuAssignedPort(): Int {
    val herokuPort = System.getenv("PORT")
    return herokuPort?.toInt() ?: 7000
}