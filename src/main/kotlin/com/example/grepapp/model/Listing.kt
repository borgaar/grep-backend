package com.example.grepapp.model

import java.util.*

data class Listing(
    val id: UUID,
    val title: String,
    val description: String,
    val author: ListingAuthor,
    val price: Int,
    val lat: Double,
    val lon: Double,
    val category: Category,
)
