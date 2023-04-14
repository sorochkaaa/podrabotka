package com.example.data.models

import kotlinx.serialization.Serializable

@Serializable
data class Report(
    val title: String?,
    val description: String?,
    val url: String?,
    val user: String?,
    val userId: String?,
)

@Serializable
data class ReportData(
    val reports: List<Report>?
)

@Serializable
data class Data(
    val data: ReportData?
)