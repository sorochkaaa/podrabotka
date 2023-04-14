package com.example.routings

import com.example.data.Query
import com.example.data.models.Data
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureReportRouting() {

    val urlSupportService = this@configureReportRouting.environment.config.property("support.url").getString()

    routing {
        authenticate {
            route("/report") {
                get {
                    val receive = call.receive<Query>()
                    val client = HttpClient(Apache) {
                        install(ContentNegotiation) {
                            json()
                        }
                    }
                    val response = client.post(urlSupportService) {
                        setBody(receive)
                        contentType(ContentType.Application.Json)
                    }
                    client.close()
                    val body: Data = response.body()
                    call.respond(body)
                }

                post {
                    val receive = call.receive<Query>()
                    val client = HttpClient(Apache) {
                        install(ContentNegotiation) {
                            json()
                        }
                    }
                    val response = client.post(urlSupportService) {
                        setBody(receive)
                        contentType(ContentType.Application.Json)
                    }
                    client.close()
                    call.respond(response.status)
                }
            }
        }
    }
}