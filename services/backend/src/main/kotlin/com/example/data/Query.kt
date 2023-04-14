package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class Query(
    val query: String
)