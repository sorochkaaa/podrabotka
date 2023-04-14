package com.example

import com.apurebase.kgraphql.Context
import com.apurebase.kgraphql.GraphQL
import com.example.data.dao.reportDao
import com.example.data.models.Report
import io.ktor.application.*
import io.ktor.features.*
import java.util.Collections

fun Application.configureGraphQL() {
    install(GraphQL) {
        playground = true

        context { call ->
            call.request.origin.remoteHost.let {
                +it
            }
        }

        schema {
            type<Report> {
                description = "User Reports"
            }

            inputType<Report> {
                name = "reportInput"
            }

            query("reports") {
                description = "Get Reports"

                resolver { userId: String?, user: String?, ctx: Context ->
                    if (userId != null) {
                        reportDao.getReportByUserId(userId, ctx.get() ?: throw BadRequestException("Invalid host"))
                    } else if (user != null) {
                        reportDao.getReportByUser(user, ctx.get() ?: throw BadRequestException("Invalid host"))
                    } else {
                        Collections.emptyList()
                    }
                }
            }

            mutation("addReport") {
                description = "Add New Report"

                resolver { input: Report, ctx: Context ->
                    reportDao.addReport(input, ctx.get() ?: throw BadRequestException("Invalid host"))
                }
            }
        }
    }
}