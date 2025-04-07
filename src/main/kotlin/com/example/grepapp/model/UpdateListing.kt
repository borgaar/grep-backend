package com.example.grepapp.model

data class UpdateListing(
    val title: String,
    val description: String,
    val price: Int,
    val category: String,
    val lat: Double,
    val lon: Double,
)