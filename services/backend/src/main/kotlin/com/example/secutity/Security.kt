package com.example.secutity

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*

fun Application.configureSecurity() {

    authentication {
        jwt {
            val secret = this@configureSecurity.environment.config.property("jwt.secret").getString()
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .build()
            )

            validate { credential ->
                if (credential.payload.getClaim("username").asString() != "" &&
                    credential.payload.getClaim("role").asString() != "") {
                    JWTPrincipal(credential.payload)
                } else null
            }

            challenge { defaultScheme, realm ->
                call.respond(HttpStatusCode.Unauthorized, "Token is not valid or has expired")
            }
        }
    }
}
