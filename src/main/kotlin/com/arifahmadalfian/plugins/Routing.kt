package com.arifahmadalfian.plugins


import com.arifahmadalfian.data.routes.getAllHeroes
import com.arifahmadalfian.data.routes.root
import com.arifahmadalfian.data.routes.searchHeroes
import io.ktor.application.*
import io.ktor.http.content.*
import io.ktor.response.*
import io.ktor.routing.*
import javax.security.sasl.AuthenticationException

fun Application.configureRouting() {
    routing {
        root()
        getAllHeroes()
        searchHeroes()

        static("/images") {
            resources("images")
        }
    }
}
