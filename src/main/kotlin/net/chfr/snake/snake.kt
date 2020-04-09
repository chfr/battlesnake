package net.chfr.snake

import io.javalin.Javalin
import io.javalin.http.Context

fun main(args: Array<String>) {
    val app = Javalin.create().start(getHerokuAssignedPort())
    app.get("/") { ctx -> ctx.result("Hello herokuuuu") }
    app.post("/ping") { ctx -> ctx.result("") }
    app.post("/start") { ctx -> ctx.result("") }
    app.post("/end") { ctx -> ctx.result("") }
    app.post("/move") { ctx -> move(ctx) }
}

fun move(context: Context) {
    val response = mapOf(
        "move" to "up"
    )
    context.json(response)
}

fun getHerokuAssignedPort(): Int {
    val herokuPort = System.getenv("PORT")
    return herokuPort?.toInt() ?: 7000
}