package com.hiperbou.plugins

import com.hiperbou.multiplayer.GameRoom
import com.hiperbou.multiplayer.GameRoomInstances
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration

fun Application.configureSockets(gameRoomInstances: GameRoomInstances) {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(30)
        //timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {
        webSocket("/api/ws/{room}") { // websocketSession
            try {
                val room = call.parameters["room"]!!
                handleSession(this, gameRoomInstances.getGameRoom(room)!!)
            } catch (e:Throwable) {
                close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "closing"))
            }
        }
    }
}

private suspend fun handleSession(ws:DefaultWebSocketServerSession, gameRoom: GameRoom) {
    for (frame in ws.incoming) {
        when (frame) {
            is Frame.Binary -> {
                val data = frame.readBytes()

                val from = data[4]
                val to = data[0]

                if(from == 1.toByte() && to == 0.toByte()) gameRoom.restart()
                gameRoom.join(from, ws)
                gameRoom.send(data, to)
            }
            else -> {}
        }
    }
    gameRoom.leave(ws)
}