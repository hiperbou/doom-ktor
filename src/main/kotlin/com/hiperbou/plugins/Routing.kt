package com.hiperbou.plugins

import com.hiperbou.service.RoomService
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(roomService: RoomService) {
    routing {
        get("/api/newroom") {
            call.respond(roomService.newRoom())
        }

        get("/api/room/{id?}") {
            val id = call.parameters["id"] ?: return@get call.respondText(
                "Missing id",
                status = HttpStatusCode.BadRequest
            )
            call.respond(roomService.room(id))
        }

        get("/version") {
            call.respondText("1.0")
        }

        // Static plugin. Try to access `/static/index.html`
        static("/") {
            resources("static")
        }
    }
}
