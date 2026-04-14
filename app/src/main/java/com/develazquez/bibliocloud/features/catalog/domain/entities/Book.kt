package com.develazquez.bibliocloud.features.catalog.domain.entities

data class Book(
    val id: String,
    val title: String,
    val author: String,
    val coverUrl: String?,
    val audioUrl: String?,
    val isAvailable: Boolean,
    val isLoanedByMe: Boolean
)
