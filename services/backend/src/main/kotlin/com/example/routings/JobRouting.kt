package com.example.routings

import com.example.data.Role
import com.example.data.dao.jobDao
import com.example.data.dao.userDao
import com.example.data.models.JobRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*

fun Application.configureJobRouting() {

    routing {
        authenticate {
            route("/jobs") {
                get {
                    call.respond(jobDao.getAllJobs())
                }

                get("/{id}") {
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal!!.payload.getClaim("username").asString()
                    val role = principal.payload.getClaim("role").asString()
                    val id = call.parameters.getOrFail<String>("id")
                    val job = jobDao.getJobById(id)
                    if (job != null) {
                        if (Role.EMPLOYEE.name == role && job.employee != username ||
                            Role.EMPLOYER.name == role && !job.users.contains(username)) {
                            job.description = ""
                        }
                        call.respond(HttpStatusCode.OK, job)
                        return@get
                    }
                    call.respond(HttpStatusCode.NotFound, "Job not found")
                }

                post {
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal!!.payload.getClaim("username").asString()
                    val role = principal.payload.getClaim("role").asString()
                    if (Role.EMPLOYER.name == role) {
                        val receive = call.receive<JobRequest>()
                        if (receive.isValidCredentials()) {
                            val newJob = jobDao.addNewJob(receive, username)
                            if (newJob != null) {
                                call.respond(HttpStatusCode.OK, newJob)
                                return@post
                            }
                        }
                    }
                    call.respond(HttpStatusCode.BadRequest)
                }

                put("/{id}/join") {
                    val principal = call.principal<JWTPrincipal>()
                    val username = principal!!.payload.getClaim("username").asString()
                    val role = principal.payload.getClaim("role").asString()
                    if (Role.EMPLOYEE.name != role) {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                    val id = call.parameters.getOrFail<String>("id")
                    val job = jobDao.getJobById(id)
                    if (job != null && !job.users.contains(username)) {
                        val newListUsers = job.users.toMutableList()
                        newListUsers.add(username)
                        jobDao.updateUsers(id, newListUsers)
                        call.respond(HttpStatusCode.OK)
                        return@put
                    }
                    call.respond(HttpStatusCode.BadRequest)
                }

                put("/{id}/select/{username}") {
                    val principal = call.principal<JWTPrincipal>()
                    val role = principal!!.payload.getClaim("role").asString()
                    val id = call.parameters.getOrFail<String>("id")
                    val selectUsername = call.parameters.getOrFail<String>("username")
                    val job = jobDao.getJobById(id)
                    val user = userDao.getUserByUsername(selectUsername)
                    if (job != null && Role.EMPLOYER.name == role &&
                        user != null && Role.EMPLOYEE.name == user.role && job.users.contains(user.username)) {
                        val newListUsers = job.users.toMutableList()
                        newListUsers.remove(selectUsername)
                        jobDao.selectEmployee(id, user.username, newListUsers)
                        call.respond(HttpStatusCode.OK)
                        return@put
                    }
                    call.respond(HttpStatusCode.BadRequest)
                }
            }
        }
    }
}