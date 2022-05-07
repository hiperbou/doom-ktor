package com.hiperbou.plugins

import com.hiperbou.extensions.resourcesWithDefault
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureWeb() {
    routing {
        static("/") {
            staticBasePackage = "static"
            resourcesWithDefault("index.htm", "static")
        }
    }
}

