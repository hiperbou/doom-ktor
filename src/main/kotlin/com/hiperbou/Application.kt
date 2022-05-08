package com.hiperbou

import com.example.plugins.configureHeaders
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.hiperbou.plugins.*
import com.hiperbou.service.InMemoryRoomService
import com.hiperbou.service.MockRoomService
import com.hiperbou.service.room.GameRoomInstances

fun main() {

    val gameRoomInstances = GameRoomInstances()
    val inMemoryRoomService = InMemoryRoomService(gameRoomInstances)

    embeddedServer(Netty,
        port = System.getenv("PORT")?.toInt() ?: 8000
    ) {
        configureHeaders()
        configureSerialization()
        configureWeb()
        configureAPI(inMemoryRoomService)
        configureSockets(gameRoomInstances)
    }.start(wait = true)
}
