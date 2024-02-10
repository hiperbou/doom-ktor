package com.hiperbou

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.hiperbou.plugins.*
import com.hiperbou.service.room.InMemoryRoomService
import com.hiperbou.multiplayer.GameRoomInstances

fun main() {

    val gameRoomInstances = GameRoomInstances()
    val inMemoryRoomService = InMemoryRoomService(gameRoomInstances)

    embeddedServer(Netty,
        port = System.getenv("PORT")?.toInt() ?: 8000,
        host = System.getenv("BIND_ADDRESS") ?: "0.0.0.0"
    ) {
        configureHeaders()
        configureCacheHeaders()
        configureSerialization()
        configureWeb()
        configureAPI(inMemoryRoomService)
        configureSockets(gameRoomInstances)
    }.start(wait = true)
}
