package com.arifahmadalfian.data.routes

import com.arifahmadalfian.data.models.ApiResponse
import com.arifahmadalfian.data.repository.IHeroRepository
import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*
import io.ktor.routing.*
import org.koin.ktor.ext.inject
import javax.security.sasl.AuthenticationException

fun Route.getAllHeroes() {
    val iHeroRepository: IHeroRepository by inject()

    get("boruto/heroes") {
        try {
            val page = call.request.queryParameters["page"]?.toInt() ?: 1
            require(page in 1..5)

            val apiResponse = iHeroRepository.getAllHeroes(page = page)
            call.respond(
                message = apiResponse,
                status = HttpStatusCode.OK
            )
        } catch (e: NumberFormatException) {
            call.respond(
                message = ApiResponse(success = false, message = "Number only"),
                status = HttpStatusCode.BadRequest
            )
        } catch (e: IllegalArgumentException) {
            call.respond(
                message = ApiResponse(success = false, message = "Not found"),
                status = HttpStatusCode.NotFound
            )
        }
    }
}