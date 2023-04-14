package com.example

import com.example.data.db.DatabaseFactory
import io.ktor.application.*
import io.ktor.features.*
import io.ktor.serialization.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    DatabaseFactory.init(environment.config)
    configureGraphQL()

    install(ContentNegotiation) {
        json()
    }
}
