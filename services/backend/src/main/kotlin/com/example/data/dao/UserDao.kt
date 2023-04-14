package com.example.data.dao

import com.example.data.RegisterRequest
import com.example.data.db.DatabaseFactory.dbQuery
import com.example.data.models.User
import com.example.data.models.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import java.util.*

class UserDao {

    private fun resultRowToUser(row: ResultRow) = User(
        id = row[Users.id].toString(),
        username = row[Users.username],
        password = row[Users.password],
        description = row[Users.description],
        role = row[Users.role]
    )

    suspend fun getUserByUsername(username: String): User? = dbQuery {
        Users.select { Users.username eq username }
            .map(::resultRowToUser)
            .singleOrNull()
    }

    suspend fun addNewUser(registerRequest: RegisterRequest): User? = dbQuery {
        val insertStatement = Users.insert {
            it[id] = UUID.randomUUID()
            it[username] = registerRequest.username
            it[password] = registerRequest.password
            it[description] = registerRequest.description
            it[role] = registerRequest.role
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToUser)
    }
}

val userDao: UserDao = UserDao()