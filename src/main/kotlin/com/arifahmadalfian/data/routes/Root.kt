package com.arifahmadalfian.data.routes

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*

fun Routing.root() {
    get("/") {
        call.respond(
            message = "Welcome to Boruto api",
            status = HttpStatusCode.OK
        )
    }
}