package com.arshalif.cashi.routes

import com.arshalif.cashi.core.remote.ApiResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.datetime.Clock

fun Route.healthRoutes() {
    route("/health") {
        get {
            val healthResponse = ApiResponse<Nothing>(
                message = "healthy at ${Clock.System.now()}",
                success = true,
            )
            call.respond(HttpStatusCode.OK, healthResponse)
        }
    }
} 