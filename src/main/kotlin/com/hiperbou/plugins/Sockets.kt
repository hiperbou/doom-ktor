package com.hiperbou.plugins

import com.hiperbou.service.room.GameRoomInstances
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class Player(val from:Byte, val ws:DefaultWebSocketServerSession)
class GameRoom(val id:String, val onDispose:()->Unit) {
    var gameStarted = false

    private val sessions = ConcurrentHashMap<Byte, Player>()
    fun join(from:Byte, ws:DefaultWebSocketServerSession) {
        if(!sessions.contains(from)) {
            sessions.put(from, Player(from, ws))
        }
    }

    fun leave(ws:DefaultWebSocketServerSession) {
        sessions.values.removeIf { it.ws == ws }
        if (sessions.isEmpty()) {
            onDispose()
        }
    }

    suspend fun send(data:ByteArray, to:Byte) {
        val player = sessions.get(to) ?: return
        player.ws.send(data.slice(4..data.lastIndex).toByteArray())
    }

    suspend fun restart() {
        sessions.values.forEach{
            it.ws.close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "closing"))
        }
        sessions.clear()
    }
}



fun Application.configureSockets(gameRoomInstances:GameRoomInstances) {
    install(WebSockets) {
        //pingPeriod = Duration.ofSeconds(15)
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

                val from = data.slice(4..7)[0]
                val to = data.slice(0..3)[0]

                if(from == 1.toByte() && to == 0.toByte()) gameRoom.restart()
                gameRoom.join(from, ws)
                gameRoom.send(data, to)
            }
            else -> {}
        }
    }
    gameRoom.leave(ws)
}