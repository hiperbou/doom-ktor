package com.hiperbou.extensions

import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

private const val pathParameterName = "static-content-path-parameter"

private fun String?.combinePackage(resourcePackage: String?) = when {
    this == null -> resourcePackage
    resourcePackage == null -> this
    else -> "$this.$resourcePackage"
}

public fun Route.resourcesWithDefault(defaultPath:String, defaultResourcePackage:String? = null, resourcePackage: String? = null) {
    val packageName = staticBasePackage.combinePackage(resourcePackage)
    get("{${pathParameterName}...}") {
        val relativePath = call.parameters.getAll(pathParameterName)?.joinToString(File.separator) ?: return@get
        val content = call.resolveResource(relativePath, packageName)
            ?: call.resolveResource(defaultPath, defaultResourcePackage)
        if (content != null) {
            call.respond(content)
        }
    }
}
