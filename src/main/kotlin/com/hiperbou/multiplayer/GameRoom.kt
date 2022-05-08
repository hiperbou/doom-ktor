package com.hiperbou.multiplayer

import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

private class Player(val ws: DefaultWebSocketServerSession)

class GameRoom(val id:String, val onDispose:()->Unit) {
    var gameStarted = false

    private val sessions = ConcurrentHashMap<Byte, Player>()
    fun join(from:Byte, ws: DefaultWebSocketServerSession) {
        if(!sessions.contains(from)) {
            sessions.put(from, Player(ws))
        }
    }

    fun leave(ws: DefaultWebSocketServerSession) {
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
