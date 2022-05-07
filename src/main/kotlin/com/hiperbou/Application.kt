package com.hiperbou

import com.example.plugins.configureHeaders
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.hiperbou.plugins.*
import com.hiperbou.service.MockRoomService

fun main() {
    embeddedServer(Netty,
        port = System.getenv("PORT")?.toInt() ?: 8000,
        host = "127.0.0.1"
    ) {
        configureHeaders()
        configureSerialization()
        configureRouting(MockRoomService())
        configureSockets()
    }.start(wait = true)
}
