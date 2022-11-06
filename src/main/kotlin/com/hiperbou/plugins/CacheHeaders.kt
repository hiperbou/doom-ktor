package com.hiperbou.plugins

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cachingheaders.*

fun Application.configureCacheHeaders() {
    val xDoomContentType = ContentType("application", "x-doom")

    install(CachingHeaders) {
        options { call, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.Html -> CachingOptions(CacheControl.NoCache(null))
                ContentType.Image.XIcon -> CachingOptions(CacheControl.MaxAge(86400))
                ContentType.Text.CSS,
                ContentType.Image.PNG,
                ContentType.Image.JPEG,
                ContentType.Image.Any,
                ContentType.Application.OctetStream,
                ContentType.Application.JavaScript,
                ContentType.Application.Wasm,
                xDoomContentType -> CachingOptions(CacheControl.MaxAge(Int.MAX_VALUE))
                else -> null
            }
        }
    }
}