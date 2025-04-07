package com.example.grepapp.dto

data class ListingUpdateRequest(
    val title: String,
    val description: String,
    val price: Int,
    val category: String,
    val location: LocationDTO,
)
