package com.hiperbou

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.hiperbou.plugins.*

fun main() {
    embeddedServer(Netty, port = System.getenv("PORT")?.toInt() ?: 8080) {
        configureRouting()
        //configureSockets()
    }.start(wait = true)
}