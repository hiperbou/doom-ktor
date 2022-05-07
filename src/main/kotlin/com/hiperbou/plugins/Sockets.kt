package com.hiperbou.plugins

import io.ktor.http.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.coroutines.isActive

class Player(val from:Byte, val ws:DefaultWebSocketServerSession)
class GameRoom() {
    private val sessions = mutableMapOf<Byte, Player>()
    var gameStarted = false
    fun join(from:Byte, ws:DefaultWebSocketServerSession) {
        if(!sessions.contains(from)) {
            sessions.put(from, Player(from, ws))
            println("player ${from} joined the game")
        }
    }

    suspend fun send(data:ByteArray, to:Byte) {
        println("sending message to ${to.toUInt()}")
        val player = sessions.get(to) ?: return
        player.ws.send(data.slice(4..data.lastIndex).toByteArray())
    }

    suspend fun restart() {
        println("Restarting")
        sessions.values.forEach{
            it.ws.close(CloseReason(CloseReason.Codes.INTERNAL_ERROR, "closing"))
        }
        sessions.clear()
    }
}

fun Application.configureSockets() {
    install(WebSockets) {
        //pingPeriod = Duration.ofSeconds(15)
        //timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    val gameRoom = GameRoom()
    suspend fun handleSession(ws:DefaultWebSocketServerSession) {
        for (frame in ws.incoming) {
            when (frame) {
                is Frame.Binary -> {
                    println("a binary thing received")
                    val data = frame.readBytes()

                    val from = data.slice(4..7)[0]
                    val to = data.slice(0..3)[0]
                    println("from $from, to $to")
                    // initial packet from doom server, let's restart
                    if(from == 1.toByte() && to == 0.toByte()) gameRoom.restart()
                    // if it's a new client, add it to the table of clients
                    gameRoom.join(from, ws)
                    // send this packet to the corresponding client
                    gameRoom.send(data, to)
                }
                /*is Frame.Text -> {
                    val text = frame.readText()
                    outgoing.send(Frame.Text("YOU SAID: $text"))
                    if (text.equals("bye", ignoreCase = true)) {
                        close(CloseReason(CloseReason.Codes.NORMAL, "Client said BYE"))
                    }
                }*/
            }
        }
    }

    routing {
        webSocket("/api/ws/{room}") { // websocketSession
            println("someone asking for ws!")
            val room = call.parameters["room"]
            println("room $room")

            handleSession(this)
            println("finished ws connection")
        }
    }

}
