package com.hiperbou.plugins

import com.hiperbou.service.RoomNotFoundResponse
import com.hiperbou.service.RoomService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Application.configureAPI(roomService: RoomService) {
    routing {
        route ("/api") {
            get("/newroom") {
                call.respond(roomService.newRoom())
            }

            get("/room/{id?}") {
                val id = call.parameters["id"] ?: return@get call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )

                val room = roomService.room(id)
                if (room != null)
                    call.respond(room)
                else
                    call.respond(HttpStatusCode.NotFound, RoomNotFoundResponse("invalid room"))
            }

            get("/room/{id?}/started") {
                val id = call.parameters["id"] ?: return@get call.respondText(
                    "Missing id",
                    status = HttpStatusCode.BadRequest
                )

                val room = roomService.setRoomStarted(id)
                if (room != null) {
                    call.respond(room)
                } else {
                    call.respond(HttpStatusCode.NotFound, RoomNotFoundResponse("invalid room"))
                }
            }

            get("/version") {
                call.respondText("1.0")
            }
        }
    }
}

