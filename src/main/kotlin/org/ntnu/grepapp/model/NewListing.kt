package com.example.grepapp.model

import java.util.*

data class NewListing(
    val title: String,
    val description: String,
    val price: Int,
    val authorPhone: String,
    val category: String,
    val lat: Double,
    val lon: Double,
) {
    val id: UUID = UUID.randomUUID()
}
