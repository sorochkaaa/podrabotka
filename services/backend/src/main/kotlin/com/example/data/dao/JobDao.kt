package com.example.data.dao

import com.example.data.db.DatabaseFactory.dbQuery
import com.example.data.models.Job
import com.example.data.models.JobRequest
import com.example.data.models.Jobs
import org.jetbrains.exposed.sql.*
import java.util.*

class JobDao {

    private fun resultRowToJob(row: ResultRow) = Job(
        id = row[Jobs.id].toString(),
        name = row[Jobs.name],
        title = row[Jobs.title],
        description = row[Jobs.description],
        employee = row[Jobs.employee],
        users = row[Jobs.users].split(", ")
    )

    private fun resultRowToHiddenJob(row: ResultRow) = Job(
        id = row[Jobs.id].toString(),
        name = row[Jobs.name],
        title = row[Jobs.title],
        description = "",
        employee = row[Jobs.employee],
        users = row[Jobs.users].split(", ")
    )

    suspend fun getAllJobs(): List<Job> = dbQuery {
        Jobs.selectAll()
            .map(::resultRowToHiddenJob)
    }

    suspend fun getJobById(id: String): Job? = dbQuery {
        Jobs.select { Jobs.id eq UUID.fromString(id) }
            .map(::resultRowToJob)
            .singleOrNull()
    }

    suspend fun addNewJob(JobRequest: JobRequest, owner: String): Job? = dbQuery {
        val insertStatement = Jobs.insert {
            it[id] = UUID.randomUUID()
            it[name] = JobRequest.name
            it[title] = JobRequest.title
            it[description] = JobRequest.description
            it[employee] = ""
            it[users] = owner
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToJob)
    }

    suspend fun updateUsers(id: String, users: List<String>): Boolean = dbQuery {
        Jobs.update({ Jobs.id eq UUID.fromString(id) }) {
            it[Jobs.users] = users.joinToString()
        } > 0
    }

    suspend fun selectEmployee(id: String, username: String, users: List<String>): Boolean = dbQuery {
        Jobs.update({ Jobs.id eq UUID.fromString(id) }) {
            it[employee] = username
            it[Jobs.users] = users.joinToString()
        } > 0
    }
}

val jobDao: JobDao = JobDao()