package com.example

import com.example.data.db.DatabaseFactory
import com.example.routings.configureJobRouting
import com.example.routings.configureMainRouting
import com.example.routings.configureReportRouting
import com.example.secutity.configureSecurity
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {

    install(CORS) {
        HttpMethod.DefaultMethods.forEach { allowMethod(it) }
        allowHeaders { true }
        allowNonSimpleContentTypes = true
        allowCredentials = true
        anyHost()
    }

    install(ContentNegotiation) {
        json()
    }

    DatabaseFactory.init(environment.config)
    configureMainRouting()
    configureJobRouting()
    configureReportRouting()
    configureSecurity()
}
