package com.example.data.dao

import com.example.data.db.DatabaseFactory.dbQuery
import com.example.data.models.Report
import com.example.data.models.Reports
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

class ReportDao {

    private fun resultRowToReport(row: ResultRow) = Report(
        title = row[Reports.title],
        description = row[Reports.description],
        url = row[Reports.url],
        user = row[Reports.user],
        userId = row[Reports.userId]
    )

    suspend fun addReport(report: Report, clientIp: String): Report? = dbQuery {
        val insertStatement = Reports.insert {
            it[id] = UUID.randomUUID()
            it[title] = report.title
            it[description] = report.description
            it[url] = report.url
            it[user] = report.user
            it[userId] = report.userId
            it[Reports.clientIp] = clientIp
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToReport)
    }

    suspend fun getReportByUserId(userId: String, clientIp: String): List<Report> = dbQuery {
        Reports.select { (Reports.clientIp eq clientIp) and (Reports.userId eq userId) }
            .map(::resultRowToReport)
    }

    suspend fun getReportByUser(user: String, clientIp: String): List<Report> = dbQuery {
        Reports.select { (Reports.clientIp eq clientIp) and (Reports.user eq user) }
            .map(::resultRowToReport)
    }
}

val reportDao: ReportDao = ReportDao()