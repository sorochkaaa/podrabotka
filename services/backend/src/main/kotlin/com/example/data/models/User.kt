package com.example.data.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class User(
    val id: String,
    val username: String,
    val password: String,
    val description: String,
    val role: String
)

object Users : Table() {
    val id = uuid("id")
    val username = varchar("username", 128)
    val password = varchar("password", 128)
    val description = varchar("description", 1024)
    val role = varchar("role", 128)

    override val primaryKey = PrimaryKey(id)
}