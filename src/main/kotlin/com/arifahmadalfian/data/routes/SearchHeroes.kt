package com.arifahmadalfian.data.routes

import com.arifahmadalfian.data.models.ApiResponse
import com.arifahmadalfian.data.repository.IHeroRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject

fun Route.searchHeroes() {
    val iHeroRepository: IHeroRepository by inject()

    get("boruto/heroes/search") {
        try {
            val name = call.request.queryParameters["name"]

            val apiResponse = iHeroRepository.searchHeroes(name = name)
            call.respond(
                message = apiResponse,
                status = HttpStatusCode.OK
            )
        } catch (e: Exception) {
            call.respond(
                message = ApiResponse(success = false, message = "Error"),
                status = HttpStatusCode.BadRequest
            )
        }
    }
}