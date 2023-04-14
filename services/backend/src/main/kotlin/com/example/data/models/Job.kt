package com.example.data.models

import com.example.data.Role
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class JobRequest(
    val name: String,
    val title: String,
    val description: String,
) {
    fun isValidCredentials(): Boolean {
        return name.isNotEmpty() && title.isNotEmpty() && description.isNotEmpty()
    }
}

@Serializable
data class Job(
    val id: String,
    val name: String,
    val title: String,
    var description: String,
    val employee: String?,
    val users: List<String>
)

object Jobs : Table() {
    val id = uuid("id")
    val name = varchar("name", 128)
    val title = varchar("title", 1024)
    val description = varchar("description", 1024)
    val employee = varchar("employee", 128)
    val users = varchar("users", 1024)

    override val primaryKey = PrimaryKey(id)
}