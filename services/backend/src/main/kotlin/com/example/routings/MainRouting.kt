package com.example.routings

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.LoginRequest
import com.example.data.RegisterRequest
import com.example.data.TokenResponse
import com.example.data.dao.userDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureMainRouting() {
    routing {
        static("/") {
            staticBasePackage = "swagger-ui"
            resources(".")
            defaultResource("index.html")
        }

        post("/register") {
            val registerRequest = call.receive<RegisterRequest>()
            if (!registerRequest.isValidCredentials()) {
                call.respond(HttpStatusCode.BadRequest, "Invalid register credentials")
                return@post
            }

            val user = userDao.getUserByUsername(registerRequest.username)
            if (user == null) {
                val newUser = userDao.addNewUser(registerRequest)
                if (newUser != null) {
                    call.respond(HttpStatusCode.OK, "Registration is completed")
                    return@post
                }
            }
            call.respond(HttpStatusCode.Conflict, "User already exist")
        }

        post("/login") {
            val loginRequest = call.receive<LoginRequest>()
            val user = userDao.getUserByUsername(loginRequest.username)
            if (user == null || user.password != loginRequest.password) {
                call.respond(HttpStatusCode.BadRequest, "Invalid password or username")
                return@post
            }

            val secret = this@configureMainRouting.environment.config.property("jwt.secret").getString()
            val token = JWT.create()
                .withClaim("username", loginRequest.username)
                .withClaim("role", user.role)
                .withExpiresAt(Date(System.currentTimeMillis() + 18000000))
                .sign(Algorithm.HMAC256(secret))
            call.respond(HttpStatusCode.OK, TokenResponse(token = token))
        }

        authenticate {
            get("/user") {
                val principal = call.principal<JWTPrincipal>()
                val username = principal!!.payload.getClaim("username").asString()
                val user = userDao.getUserByUsername(username)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                    return@get
                }
                call.respond(HttpStatusCode.NotFound, "User not found")
            }
        }
    }
}