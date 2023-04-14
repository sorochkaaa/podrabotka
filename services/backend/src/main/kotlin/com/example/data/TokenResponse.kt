package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    val token: String
)