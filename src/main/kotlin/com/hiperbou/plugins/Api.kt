package com.hiperbou.plugins

import com.hiperbou.service.room.RoomNotFoundResponse
import com.hiperbou.service.room.RoomResponse
import com.hiperbou.service.room.RoomService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.pipeline.*

fun Application.configureAPI(roomService: RoomService) {
    routing {
        route ("/api") {
            get("/newroom") {
                call.respond(roomService.newRoom())
            }

            get("/room/{id?}") {
                roomResponse { roomService.room(it) }
            }

            get("/room/{id?}/started") {
                roomResponse { roomService.setRoomStarted(it) }
            }

            get("/version") {
                call.respondText("1.0")
            }
        }
    }
}

private suspend fun PipelineContext<Unit, ApplicationCall>.roomResponse(check:(String)-> RoomResponse?) {
    val id = call.parameters["id"] ?: return call.respondText(
        "Missing id",
        status = HttpStatusCode.BadRequest
    )

    val room = check(id)
    if (room != null) {
        call.respond(room)
    } else {
        call.respond(HttpStatusCode.NotFound, RoomNotFoundResponse("invalid room"))
    }
}

