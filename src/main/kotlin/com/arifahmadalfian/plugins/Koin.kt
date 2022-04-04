package com.arifahmadalfian.plugins

import com.arifahmadalfian.data.di.koinModule
import io.ktor.application.*
import org.koin.ktor.ext.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin(){
    install(Koin){
        slf4jLogger()
        modules(koinModule)
    }
}