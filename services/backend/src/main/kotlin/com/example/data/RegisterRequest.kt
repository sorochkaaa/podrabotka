package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val description: String,
    val role: String
) {
    fun isValidCredentials(): Boolean {
        return username.isNotEmpty() && password.isNotEmpty() &&
                (role == Role.EMPLOYER.name || role == Role.EMPLOYEE.name)
    }
}
