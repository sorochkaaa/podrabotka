package com.example.data.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Report(
    val title: String,
    val description: String,
    val url: String,
    val user: String,
    val userId: String
)

object Reports : Table() {
    val id = uuid("id")
    val title = varchar("title", 1024)
    val description = varchar("description", 1024)
    val url = varchar("url", 1024)
    val user = varchar("user", 1024)
    val userId = varchar("user_id", 1024)
    val clientIp = varchar("client_ip", 1024)

    override val primaryKey = PrimaryKey(id)
}
