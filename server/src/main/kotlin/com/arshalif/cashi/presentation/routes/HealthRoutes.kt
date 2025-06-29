package com.arshalif.cashi.presentation.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.datetime.Clock

fun Route.healthRoutes() {
    route("/health") {
        get {
            call.respondText(
                """{"status":"healthy","timestamp":"${Clock.System.now()}"}""",
                status = HttpStatusCode.OK,
                contentType = ContentType.Application.Json
            )
        }
    }
} 